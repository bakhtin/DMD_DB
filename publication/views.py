from django.http import Http404
from django.shortcuts import render_to_response, redirect
from django.template import RequestContext
from django.views.decorators.http import require_http_methods
from django.db import connection
import operator
from manager.user import *


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
                cursor.execute("SELECT p.id, title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number, "
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