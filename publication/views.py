from django.http import Http404
from django.shortcuts import render_to_response, redirect
from django.template import RequestContext
from django.views.decorators.http import require_http_methods
from django.db import connection
import operator
from manager.user import *
from .forms import *
from django.db import IntegrityError


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
    except:
        return redirect('authentication')


@require_http_methods(['GET', 'POST'])
def publication_add(request):
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
                thesaurus_terms_keywords = thesaurus_terms_keywords.split(';')
            controlled_terms_keywords = add_form.cleaned_data['controlled_terms_keywords']
            if controlled_terms_keywords:
                controlled_terms_keywords = controlled_terms_keywords.split(';')
            uncontrolled_terms_keywords = add_form.cleaned_data['uncontrolled_terms_keywords']
            if uncontrolled_terms_keywords:
                uncontrolled_terms_keywords = uncontrolled_terms_keywords.split(';')
            authors = add_form.cleaned_data['authors']
            if authors:
                authors = authors.split(';')
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
                    cursor.execute("insert into auhtors(name) VALUES (%s)", [kw])
                    authors_last_ids.append(int(cursor.fetchone()[0]))
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
                cursor.execute("insert INTO publication(title, issn, isbn, doi, pubdate, pages, volume, abstract, url, "
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
                        cursor.execute("insert into publication_keyword(keyword_id, publication_id, type) VALUES "
                                       "(%s, %s, %s)", [kw, publication_last_id, kw_type])
                    except:
                        continue

            for author in authors_last_ids:
                try:
                    cursor.execute("insert into publication_author(publication_id, author_id) VALUES "
                                   "(%s, %s)", [publication_last_id, author])
                except:
                    pass
            error_message = 'Successfully inserted!'
        else:
            return render_to_response('publication_add.html', {'add_form': add_form},
                                              context_instance=RequestContext(request))
    add_form = PubAdditionForm()
    return render_to_response('publication_add.html', {'add_form': add_form,
                                                       'error_message': error_message},
                                              context_instance=RequestContext(request))