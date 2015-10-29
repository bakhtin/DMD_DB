from django.http import Http404, HttpResponse, HttpResponseBadRequest
from django.shortcuts import render_to_response, redirect
from django.template import RequestContext
from django.views.decorators.http import require_http_methods
from django.db import connection
import operator
from manager.user import *
from .forms import *
from django.db import IntegrityError
from dbms_web.custom_exception import *


def dictfetchall(cursor):
    # Return all rows from a cursor as a dict
    columns = [col[0] for col in cursor.description]
    return [
        dict(zip(columns, row))
        for row in cursor.fetchall()
    ]


def get_relevant_publications_by_keyword(publication_id, relevant_factor):
    res = {}
    cursor = connection.cursor()
    cursor.execute("select keyword_id from publication_keyword where publication_id=%s", [int(publication_id)])
    a = cursor.fetchall()

    # get top 'relevant_factor' number of publications with keywords from 'publication_id'
    for e in a:
        res[e[0]] = []
        cursor.execute("select publication_id from publication_keyword where keyword_id=%s limit 0, %s",
                       [int(e[0]), int(relevant_factor)])
        c = cursor.fetchall()
        for i in c:
            if i[0] != publication_id:
                res[e[0]].append(i[0])

    # get sets of intersections of each set with each set
    r = []
    for k, v in res.items():
        for i, j in res.items():
            if v != j:
                intersection = set(v).intersection(set(j))
                if intersection:
                    r.append(intersection)

    # how many of each element in sets
    c = {}
    for sets in r:
        for element in sets:
            element = int(element)
            if element in c:
                c[element] += 1
            else:
                c[element] = 0

    # sort by the number of occurrences
    sorted_x = sorted(c.items(), key=operator.itemgetter(1), reverse=True)
    if sorted_x:
        # return top 5 publications
        if len(sorted_x) > 5:
            sorted_x = sorted_x[:5]

    return sorted_x


@require_http_methods(['GET'])
def publication_full(request, publication_id):
    # check authenticated
    try:
        if is_authenticated(request.COOKIES['session_id']):
            # go ahead, authorized user
            cursor = connection.cursor()
            try:
                publication_id = int(publication_id)
                cursor.execute(
                    "SELECT p.id, title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number, "
                    "i_name.name, i_type.type, aff.name, pshr.name "
                    "FROM publication p, issue_name i_name, issue_type i_type, affiliation aff, publisher pshr "
                    "WHERE p.id = %s "
                    "and p.issue_name_id=i_name.id "
                    "and p.issue_type_id=i_type.id "
                    "and p.affiliation_id=aff.id "
                    "and p.publisher_id=pshr.id", [publication_id])
                publication = dictfetchall(cursor)[0]

                cursor.execute("SELECT t1.author_id, name FROM author, "
                               "(SELECT author_id "
                               "FROM publication_author "
                               "WHERE publication_author.publication_id = %s) AS t1 "
                               "WHERE author.id=t1.author_id", [publication_id])
                # publication_authors = cursor.fetchall()
                publication_authors = dictfetchall(cursor)

                cursor.execute("SELECT word, t1.type FROM keyword, "
                               "(SELECT keyword_id, type FROM publication_keyword WHERE publication_id = %s) AS t1 "
                               "WHERE keyword.id=t1.keyword_id", [publication_id])
                publication_keywords = cursor.fetchall()

                # group by keyword type
                words = {}
                for word, type in publication_keywords:
                    words.setdefault(type, []).append(word)

                publication_keywords = words

                # retrieve relevant publications
                relevant_publications_details = []
                relevant_publications_by_keyword = get_relevant_publications_by_keyword(publication_id, 1000)
                if relevant_publications_by_keyword:
                    relevant_publications_details = []
                    for publication_element in relevant_publications_by_keyword:
                        cursor.execute("select id, title from publication where id=%s", [publication_element[0]])
                        relevant_publications_details.append(dictfetchall(cursor)[0])

                if publication:
                    return render_to_response('publication.html', {'publications': publication,
                                                                   'publication_authors': publication_authors,
                                                                   'publication_keywords': publication_keywords,
                                                                   'relevant_pubs': relevant_publications_details},
                                              context_instance=RequestContext(request))
                else:
                    raise Http404("No such publication :(")
            except:
                raise Http404("Not a number")

        # go authorize first, maaaan
        else:
            return redirect('authentication')

    # go authorize first, maaaan
    except NotAuthenticatedException:
        return redirect('authentication')


@require_http_methods(['GET', 'POST'])
def publication_add(request):
    # check authenticated
    try:
        if is_authenticated(request.COOKIES['session_id']) and \
                (check_role(request.COOKIES['session_id']) in ['admin', 'modify']):
            # go ahead, authorized user
            if request.method == 'GET':
                add_form = PubAdditionForm()
                return render_to_response('publication_add.html', {'add_form': add_form},
                                          context_instance=RequestContext(request))
            else:
                error_message = ''
                add_form = PubAdditionForm(request.POST)
                if add_form.is_valid():
                    # Many-to-many
                    thesaurus_terms_keywords = add_form.cleaned_data['thesaurus_terms_keywords']
                    if thesaurus_terms_keywords:
                        thesaurus_terms_keywords = [line for line in thesaurus_terms_keywords.split('\r\n') if
                                                    line.strip() != '']
                    controlled_terms_keywords = add_form.cleaned_data['controlled_terms_keywords']
                    if controlled_terms_keywords:
                        controlled_terms_keywords = [line for line in controlled_terms_keywords.split('\r\n') if
                                                     line.strip() != '']
                    uncontrolled_terms_keywords = add_form.cleaned_data['uncontrolled_terms_keywords']
                    if uncontrolled_terms_keywords:
                        uncontrolled_terms_keywords = [line for line in uncontrolled_terms_keywords.split('\r\n') if
                                                       line.strip() != '']
                    authors = add_form.cleaned_data['authors']
                    if authors:
                        authors = [line for line in authors.split('\r\n') if line.strip() != '']
                    # FKs
                    publisher = add_form.cleaned_data['publisher']
                    affiliation = add_form.cleaned_data['affiliation']
                    issue_name = add_form.cleaned_data['issue_name']
                    issue_type = add_form.cleaned_data['issue_type']
                    # the rest
                    title = add_form.cleaned_data['title']
                    issn = add_form.cleaned_data['issn']
                    isbn = add_form.cleaned_data['isbn']
                    doi = add_form.cleaned_data['doi']
                    pubdate = add_form.cleaned_data['pubdate']
                    pages = add_form.cleaned_data['pages']
                    volume = add_form.cleaned_data['volume']
                    abstract = add_form.cleaned_data['abstract']
                    url = add_form.cleaned_data['url']
                    pub_number = add_form.cleaned_data['pub_number']

                    # start processing form
                    cursor = connection.cursor()
                    kwds_last_ids = {}

                    # better use transactions, ofc. But our DB does not support it :(

                    # process thesaurusterms
                    for kw in thesaurus_terms_keywords:
                        try:
                            cursor.execute("insert into keyword(word) VALUES (%s)", [kw])
                            if 'thesaurusterms' not in kwds_last_ids:
                                kwds_last_ids['thesaurusterms'] = []
                            kwds_last_ids['thesaurusterms'].append(int(cursor.lastrowid))

                            # insert after publication_id will be known
                            # cursor.execute("insert into publication_keyword(publication_id, keyword_id) VALUES ()")
                        except IntegrityError:
                            cursor.execute("select id from keyword where word=%s", [kw])
                            last_id = int(cursor.fetchone()[0])
                            cursor.execute("select type from publication_keyword where keyword_id=%s", [last_id])
                            last_type = cursor.fetchone()[0]
                            if last_type not in kwds_last_ids:
                                kwds_last_ids[last_type] = []
                            kwds_last_ids[last_type].append(last_id)

                    # process controlled_terms_keywords
                    for kw in controlled_terms_keywords:
                        try:
                            cursor.execute("insert into keyword(word) VALUES (%s)", [kw])
                            if 'controlledterms' not in kwds_last_ids:
                                kwds_last_ids['controlledterms'] = []
                            kwds_last_ids['controlledterms'].append(int(cursor.lastrowid))

                            # insert after publication_id will be known
                            # cursor.execute("insert into publication_keyword(publication_id, keyword_id) VALUES ()")
                        except IntegrityError:
                            cursor.execute("select id from keyword where word=%s", [kw])
                            last_id = int(cursor.fetchone()[0])
                            cursor.execute("select type from publication_keyword where keyword_id=%s", [last_id])
                            last_type = cursor.fetchone()[0]
                            if last_type not in kwds_last_ids:
                                kwds_last_ids[last_type] = []
                            kwds_last_ids[last_type].append(last_id)

                    # process uncontrolled_terms_keywords
                    for kw in uncontrolled_terms_keywords:
                        try:
                            cursor.execute("insert into keyword(word) VALUES (%s)", [kw])
                            if 'uncontrolledterms' not in kwds_last_ids:
                                kwds_last_ids['uncontrolledterms'] = []
                            kwds_last_ids['uncontrolledterms'].append(int(cursor.lastrowid))

                            # insert after publication_id will be known
                            # cursor.execute("insert into publication_keyword(publication_id, keyword_id) VALUES ()")
                        except IntegrityError:
                            cursor.execute("select id from keyword where word=%s", [kw])
                            last_id = int(cursor.fetchone()[0])
                            cursor.execute("select type from publication_keyword where keyword_id=%s", [last_id])
                            last_type = cursor.fetchone()[0]
                            if last_type not in kwds_last_ids:
                                kwds_last_ids[last_type] = []
                            kwds_last_ids[last_type].append(last_id)

                    # process authors
                    authors_last_ids = []
                    for kw in authors:
                        try:
                            cursor.execute("insert into author(name) VALUES (%s)", [kw])
                            authors_last_ids.append(int(cursor.lastrowid))
                        except IntegrityError:
                            cursor.execute("select id from author where name=%s", [kw])
                            authors_last_ids.append(int(cursor.fetchone()[0]))

                    # process publisher
                    try:
                        cursor.execute("insert INTO publisher(name) VALUES (%s)", [publisher])
                        publisher_last_id = cursor.lastrowid
                    except IntegrityError:
                        cursor.execute("select id from publisher WHERE name=%s", [publisher])
                        publisher_last_id = int(cursor.fetchone()[0])

                    # process affiliation
                    try:
                        cursor.execute("insert INTO affiliation(name) VALUES (%s)", [affiliation])
                        affiliation_last_id = cursor.lastrowid
                    except IntegrityError:
                        cursor.execute("select id from affiliation WHERE name=%s", [affiliation])
                        affiliation_last_id = int(cursor.fetchone()[0])

                    # process issue_name
                    try:
                        cursor.execute("insert INTO issue_name(name) VALUES (%s)", [issue_name])
                        issue_name_last_id = cursor.lastrowid
                    except IntegrityError:
                        cursor.execute("select id from issue_name WHERE name=%s", [issue_name])
                        issue_name_last_id = int(cursor.fetchone()[0])

                    # process issue_type
                    try:
                        cursor.execute("insert INTO issue_type(type) VALUES (%s)", [issue_type])
                        issue_type_last_id = cursor.lastrowid
                    except IntegrityError:
                        cursor.execute("select id from issue_type WHERE type=%s", [issue_type])
                        issue_type_last_id = int(cursor.fetchone()[0])

                    # process publication itself
                    try:
                        cursor.execute(
                            "insert INTO publication(title, issn, isbn, doi, pubdate, pages, volume, abstract, url, "
                            "pub_number, issue_name_id, issue_type_id, affiliation_id, publisher_id) VALUES "
                            "(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                            [title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number,
                             issue_name_last_id, issue_type_last_id, affiliation_last_id, publisher_last_id])

                        publication_last_id = cursor.lastrowid
                    except IntegrityError:
                        error_message = 'Duplicate record'
                        return render_to_response('publication_add.html', {'error_message': error_message},
                                                  context_instance=RequestContext(request))
                    except:
                        error_message = 'General error'
                        return render_to_response('publication_add.html', {'error_message': error_message},
                                                  context_instance=RequestContext(request))

                    # don't know how to handle exceptions on insertion

                    for kw_type, kws in kwds_last_ids.items():
                        for kw in kws:
                            try:
                                cursor.execute(
                                    "insert into publication_keyword(keyword_id, publication_id, type) VALUES "
                                    "(%s, %s, %s)", [kw, publication_last_id, kw_type])
                            except:
                                continue

                    for author in authors_last_ids:
                        try:
                            cursor.execute("insert into publication_author(publication_id, author_id) VALUES "
                                           "(%s, %s)", [publication_last_id, author])
                        except:
                            continue
                    error_message = 'Successfully inserted! ID is %s' % publication_last_id
                else:
                    return render_to_response('publication_add.html', {'add_form': add_form},
                                              context_instance=RequestContext(request))
            add_form = PubAdditionForm()
            return render_to_response('publication_add.html', {'add_form': add_form,
                                                               'error_message': error_message},
                                      context_instance=RequestContext(request))

        # go authorize first, maaaan
        else:
            return redirect('authentication')

    # go authorize first, maaaan
    except NotAuthenticatedException:
        return redirect('authentication')


@require_http_methods(['GET', 'POST'])
def publication_edit(request, publication_id):
    # check authenticated
    try:
        if is_authenticated(request.COOKIES['session_id']) and \
                (check_role(request.COOKIES['session_id']) in ['admin', 'modify']):
            # go ahead, authorized user
            if request.method == 'GET':
                cursor = connection.cursor()
                # fetching form data
                # fetching basic info
                cursor.execute(
                    "SELECT p.id, title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number, "
                    "i_name.name as issue_name, i_type.type as issue_type, "
                    "aff.name as affiliation, pshr.name as publisher "
                    "FROM publication p, issue_name i_name, issue_type i_type, affiliation aff, publisher pshr "
                    "WHERE p.id = %s "
                    "and p.issue_name_id=i_name.id "
                    "and p.issue_type_id=i_type.id "
                    "and p.affiliation_id=aff.id "
                    "and p.publisher_id=pshr.id", [publication_id])
                try:
                    publication_info = dictfetchall(cursor)[0]
                except:
                    raise Http404('No such publication')
                # fetching keywords
                cursor.execute("select word, t1.type from keyword, "
                               "(select keyword_id, type from publication_keyword where publication_id = %s) as t1"
                               " where keyword.id=t1.keyword_id",
                               [publication_id])

                publication_kwds = cursor.fetchall()

                # group by keyword type
                words = {}
                for word, type in publication_kwds:
                    words.setdefault(type, []).append(word)

                publication_kwds = words

                # fetch thesaurusterms
                publication_thesaurusterms = ''
                if 'thesaurusterms' in publication_kwds:
                    for word in publication_kwds['thesaurusterms']:
                        publication_thesaurusterms += '%s\r\n' % word
                    del publication_kwds['thesaurusterms']

                # fetch controlledterms
                publication_controlledterms = ''
                if 'controlledterms' in publication_kwds:
                    for word in publication_kwds['controlledterms']:
                        publication_controlledterms += '%s\r\n' % word
                    del publication_kwds['controlledterms']
                # fetch uncontrolledterms
                publication_uncontrolledterms = ''
                if 'uncontrolledterms' in publication_kwds:
                    for word in publication_kwds['uncontrolledterms']:
                        publication_uncontrolledterms += '%s\r\n' % word
                    del publication_kwds['uncontrolledterms']

                cursor.execute("select name from author, (select author_id from publication_author "
                               "where publication_author.publication_id = %s) as t1 "
                               "where author.id=t1.author_id", [publication_id])
                publication_authors = cursor.fetchall()
                if publication_authors:
                    publication_authors = '\n'.join([x[0] for x in publication_authors])

                edit_form = PubAdditionForm(initial={'title': publication_info['title'],
                                                     'issn': publication_info['issn'],
                                                     'isbn': publication_info['isbn'],
                                                     'doi': publication_info['doi'],
                                                     'pubdate': publication_info['pubdate'],
                                                     'pages': publication_info['pages'],
                                                     'volume': publication_info['volume'],
                                                     'abstract': publication_info['abstract'],
                                                     'url': publication_info['url'],
                                                     'pub_number': publication_info['pub_number'],
                                                     'issue_name': publication_info['issue_name'],
                                                     'issue_type': publication_info['issue_type'],
                                                     'affiliation': publication_info['affiliation'],
                                                     'publisher': publication_info['publisher'],
                                                     'authors': publication_authors,
                                                     'thesaurus_terms_keywords': publication_thesaurusterms,
                                                     'controlled_terms_keywords': publication_controlledterms,
                                                     'uncontrolled_terms_keywords': publication_uncontrolledterms})

                return render_to_response('publication_edit.html',
                                          {'edit_form': edit_form},
                                          context_instance=RequestContext(request))
            else:
                cursor = connection.cursor()

                error_message = ''
                edit_form = PubAdditionForm(request.POST)
                if edit_form.is_valid():
                    # Many-to-many
                    thesaurus_terms_keywords = edit_form.cleaned_data['thesaurus_terms_keywords']
                    thesaurus_terms_keywords_init = thesaurus_terms_keywords
                    if thesaurus_terms_keywords:
                        thesaurus_terms_keywords = [line for line in thesaurus_terms_keywords.split('\r\n') if
                                                    line.strip() != '']
                    controlled_terms_keywords = edit_form.cleaned_data['controlled_terms_keywords']
                    controlled_terms_keywords_init = controlled_terms_keywords
                    if controlled_terms_keywords:
                        controlled_terms_keywords = [line for line in controlled_terms_keywords.split('\r\n') if
                                                     line.strip() != '']
                    uncontrolled_terms_keywords = edit_form.cleaned_data['uncontrolled_terms_keywords']
                    uncontrolled_terms_keywords_init = uncontrolled_terms_keywords
                    if uncontrolled_terms_keywords:
                        uncontrolled_terms_keywords = [line for line in uncontrolled_terms_keywords.split('\r\n') if
                                                       line.strip() != '']
                    authors = edit_form.cleaned_data['authors']
                    authors_init = authors
                    if authors:
                        authors = [line for line in authors.split('\r\n') if line.strip() != '']
                    # FKs
                    publisher = edit_form.cleaned_data['publisher']
                    affiliation = edit_form.cleaned_data['affiliation']
                    issue_name = edit_form.cleaned_data['issue_name']
                    issue_type = edit_form.cleaned_data['issue_type']
                    # the rest
                    title = edit_form.cleaned_data['title']
                    issn = edit_form.cleaned_data['issn']
                    isbn = edit_form.cleaned_data['isbn']
                    doi = edit_form.cleaned_data['doi']
                    pubdate = edit_form.cleaned_data['pubdate']
                    pages = edit_form.cleaned_data['pages']
                    volume = edit_form.cleaned_data['volume']
                    abstract = edit_form.cleaned_data['abstract']
                    url = edit_form.cleaned_data['url']
                    pub_number = edit_form.cleaned_data['pub_number']

                    # we don't bookkeep old records. i.e. just remove FK in MxM table, but keep record itself
                    # all update process should be one single transaction in case something goes wrong
                    # But our DB doesn't have transactions :(. We cannot rollback partial insertions

                    # processing author update
                    authors_publication_has = []
                    authors_publication_acquire = []

                    # which authors we already have in publication
                    cursor.execute("select name, t1.author_id from author, (select author_id from publication_author "
                                   "where publication_author.publication_id = %s) as t1 "
                                   "where author.id=t1.author_id", [publication_id])
                    a = dictfetchall(cursor)
                    for author in a:
                        authors_publication_has.append((int(publication_id), int(author['author_id']), author['name']))

                    # which authors we don't have in our publication but want to add
                    for author in authors:
                        cursor.execute("select id, name from author where name=%s", [author])
                        try:
                            # which authors are already in DB but not linked to this publication
                            a = dictfetchall(cursor)[0]
                            authors_publication_acquire.append((int(publication_id), int(a['id']), a['name']))
                        except:
                            # which authors are entirely new in our DB
                            authors_publication_acquire.append((int(publication_id), 0, author))

                    authors_to_append = set(authors_publication_acquire) - set(authors_publication_has)
                    authors_to_delete = set(authors_publication_has) - set(authors_publication_acquire)

                    for author in authors_to_append:
                        # if author doesn't exist in DB yet add it
                        if author[1] == 0:
                            cursor.execute("insert into author(name) VALUES (%s)", [author[2]])
                            cursor.execute("insert into publication_author(publication_id, author_id) VALUES (%s,%s)",
                                           [publication_id, cursor.lastrowid])
                        # if author already in DB but not linked to this publication yet -> link it
                        else:
                            cursor.execute("insert into publication_author(publication_id, author_id) VALUES (%s,%s)",
                                           [publication_id, author[1]])
                    for author in authors_to_delete:
                        cursor.execute("delete from publication_author where publication_id=%s and author_id=%s",
                                       [publication_id, author[1]])

                    # processing keyword update
                    kwds_publication_has = []
                    kwds_publication_acquire = []
                    publication_kwds = {'thesaurusterms': thesaurus_terms_keywords,
                                        'controlledterms': controlled_terms_keywords,
                                        'uncontrolledterms': uncontrolled_terms_keywords}

                    # which keywords we already have in publication
                    cursor.execute("select word, t1.keyword_id, t1.type from keyword, (select keyword_id, type from "
                                   "publication_keyword where publication_keyword.publication_id = %s) as t1 "
                                   "where keyword.id=t1.keyword_id", [publication_id])
                    a = dictfetchall(cursor)
                    for word in a:
                        kwds_publication_has.append(
                            (int(publication_id), int(word['keyword_id']), word['word'], word['type']))

                    # which keywords we don't have in our publication but want to add
                    for type, words in publication_kwds.items():
                        for word in words:
                            cursor.execute("select id from keyword where word=%s", [word])
                            try:
                                # which keywords are already in DB but not linked to this publication
                                a = dictfetchall(cursor)[0]
                                kwds_publication_acquire.append((int(publication_id), int(a['id']), word, type))
                            except:
                                # which keywords are entirely new in our DB
                                kwds_publication_acquire.append((int(publication_id), 0, word, type))

                    kwds_to_append = set(kwds_publication_acquire) - set(kwds_publication_has)
                    kwds_to_delete = set(kwds_publication_has) - set(kwds_publication_acquire)

                    for word in kwds_to_append:
                        if word[1] == 0:
                            cursor.execute("insert into keyword(word) VALUES (%s)", [word[2]])
                            cursor.execute(
                                "insert into publication_keyword(publication_id, keyword_id, type) VALUES (%s,%s,%s)",
                                [publication_id, cursor.lastrowid, word[3]])
                        else:
                            cursor.execute(
                                "insert into publication_keyword(publication_id, keyword_id, type) VALUES (%s,%s,%s)",
                                [publication_id, word[1], word[3]])
                    for word in kwds_to_delete:
                        cursor.execute("delete from publication_keyword where publication_id=%s and keyword_id=%s",
                                       [publication_id, word[1]])

                    # processing publisher
                    cursor.execute("select id from publisher WHERE name=%s", [publisher])
                    try:
                        publisher_last_id = cursor.fetchone()[0]
                    except:
                        cursor.execute("insert into publisher(name) VALUES (%s)", [publisher])
                        publisher_last_id = cursor.lastrowid

                    # processing affiliation
                    cursor.execute("select id from affiliation WHERE name=%s", [affiliation])
                    try:
                        affiliation_last_id = cursor.fetchone()[0]
                    except:
                        cursor.execute("insert into affiliation(name) VALUES (%s)", [affiliation])
                        affiliation_last_id = cursor.lastrowid

                    # processing issue_name
                    cursor.execute("select id from issue_name WHERE name=%s", [issue_name])
                    try:
                        issue_name_last_id = cursor.fetchone()[0]
                    except:
                        cursor.execute("insert into issue_name(name) VALUES (%s)", [issue_name])
                        issue_name_last_id = cursor.lastrowid

                    # processing issue_type
                    cursor.execute("select id from issue_type WHERE type=%s", [issue_type])
                    try:
                        issue_type_last_id = cursor.fetchone()[0]
                    except:
                        cursor.execute("insert into issue_type(name) VALUES (%s)", [issue_type])
                        issue_type_last_id = cursor.lastrowid

                    # processing publication itself. Update it
                    # title, issn, isbn, doi , pubdate , pages , volume , abstract , url , pub_number, publisher, affiliation, issue_name, issue_type
                    try:
                        cursor.execute(
                            "update publication set title=%s, issn=%s, isbn=%s, doi=%s, pubdate=%s, pages=%s, volume=%s,"
                            "abstract=%s, url=%s, pub_number=%s, publisher_id=%s, affiliation_id=%s, issue_name_id=%s,"
                            "issue_type_id=%s where id=%s",
                            [title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number,
                             publisher_last_id, affiliation_last_id, issue_name_last_id,
                             issue_type_last_id, publication_id])
                        error_message = 'Publication #%s successfully updated' % publication_id

                        edit_form = PubAdditionForm(initial={'title': title,
                                                             'issn': issn,
                                                             'isbn': isbn,
                                                             'doi': doi,
                                                             'pubdate': pubdate,
                                                             'pages': pages,
                                                             'volume': volume,
                                                             'abstract': abstract,
                                                             'url': url,
                                                             'pub_number': pub_number,
                                                             'issue_name': issue_name,
                                                             'issue_type': issue_type,
                                                             'affiliation': affiliation,
                                                             'publisher': publisher,
                                                             'authors': authors_init,
                                                             'thesaurus_terms_keywords': thesaurus_terms_keywords_init,
                                                             'controlled_terms_keywords': controlled_terms_keywords_init,
                                                             'uncontrolled_terms_keywords': uncontrolled_terms_keywords_init})
                    except IntegrityError:
                        error_message = 'Duplicate record'

                return render_to_response('publication_edit.html',
                                          {'edit_form': edit_form,
                                           'error_message': error_message},
                                          context_instance=RequestContext(request))
                # go authorize first, maaaan
        else:
            return redirect('authentication')

    # go authorize first, maaaan
    except NotAuthenticatedException:
        return redirect('authentication')


@require_http_methods(['GET', 'POST'])
def search_publication(request):
    if request.method == 'GET':
        cursor = connection.cursor()
        cursor.execute("select id, title from publication order by id desc limit 0, 7")
        recent_publications = dictfetchall(cursor)

        return render_to_response(['main.html', 'recent_publications.html'],
                                  {'recent_publications': recent_publications},
                                  context_instance=RequestContext(request))


@require_http_methods(['POST'])
def publication_delete(request):
    # check authenticated
    try:
        if is_authenticated(request.COOKIES['session_id']) and \
                (check_role(request.COOKIES['session_id']) in ['admin', 'modify']):
            try:
                publication_id = int(request.POST.get('publication_id'))
                cursor = connection.cursor()
                cursor.execute(
                    "select publisher_id, affiliation_id, issue_name_id, issue_type_id from publication where id=%s",
                    [publication_id])
                try:
                    publication_fks = dictfetchall(cursor)[0]

                    # fetching keywords
                    cursor.execute("select keyword_id from publication_keyword where publication_id=%s",
                                   [publication_id])
                    publication_kwds = cursor.fetchall()

                    # fetching authors
                    cursor.execute("select author_id from publication_author where publication_id=%s", [publication_id])
                    publication_authors = cursor.fetchall()

                    # remove affiliation if nobody use it
                    cursor.execute("select id from publication where affiliation_id = %s limit 0,1",
                                   [publication_fks['affiliation_id']])
                    if not cursor.fetchone():
                        cursor.execute("delete from affiliation where id=%s", [publication_fks['affiliation_id']])

                    # remove publisher if nobody use it
                    cursor.execute("select id from publication where publisher_id = %s limit 0,1",
                                   [publication_fks['publisher_id']])
                    if not cursor.fetchone():
                        cursor.execute("delete from publisher where id=%s", [publication_fks['publisher_id']])

                    # remove issue_name if nobody use it
                    cursor.execute("select id from publication where issue_name_id = %s limit 0,1",
                                   [publication_fks['issue_name_id']])
                    if not cursor.fetchone():
                        cursor.execute("delete from issue_name where id=%s", [publication_fks['issue_name_id']])

                    # remove issue_type if nobody use it
                    cursor.execute("select id from publication where issue_type_id = %s limit 0,1",
                                   [publication_fks['issue_type_id']])
                    if not cursor.fetchone():
                        cursor.execute("delete from issue_type where id=%s", [publication_fks['issue_type_id']])

                    # remove keyword if nobody use it
                    for kw_id in publication_kwds:
                        cursor.execute("select publication_id from publication_keyword where keyword_id = %s limit 0,1",
                                       [kw_id])
                        if not cursor.fetchone():
                            cursor.execute("delete from keyword where id=%s", [kw_id])

                    # remove author if nobody use it
                    for author_id in publication_authors:
                        cursor.execute("select publication_id from publication_author where author_id = %s limit 0,1",
                                       [author_id])
                        if not cursor.fetchone():
                            cursor.execute("delete from author where id=%s", [author_id])
                    cursor.execute("delete from publication where id=%s", [publication_id])
                    return HttpResponse('Publication %s successfully removed' % cursor.lastrowid)
                except:
                    # No such publication. Really!
                    raise Http404("No such publication")
            except:
                return HttpResponseBadRequest('')
        # go authorize first, maaaan
        else:
            return redirect('authentication')

    # go authorize first, maaaan
    except NotAuthenticatedException:
        return redirect('authentication')