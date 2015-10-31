from django.db import connection
from django.http import Http404
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.views.decorators.http import require_http_methods
from django.http import JsonResponse

def dictfetchall(cursor):
    # Return all rows from a cursor as a dict
    columns = [col[0] for col in cursor.description]
    return [
        dict(zip(columns, row))
        for row in cursor.fetchall()
    ]

@require_http_methods(['GET'])
def related_articles(request, author_id, page, limit):
    cursor = connection.cursor()
    try:
        author_id = int(author_id)
        page = int(page)
        limit = int(limit)

        start = (int(page) - 1) * limit

        cursor.execute("select t1.publication_id from publication as p, "
                           "(select publication_id from publication_author where author_id=%s) as t1, "
                           "(select id, name from author where id=%s) as t2 "
                           "where t1.publication_id=p.id limit %s,%s", [author_id, author_id, start + limit, limit])
        if len(cursor.fetchall()) > 0:
            next_page = page + 1
        else:
            next_page = -1

        if 0 <= limit <= 100:
            cursor.execute("SELECT t1.publication_id, t2.name, t2.id as a_id, title, abstract "
                       "FROM publication AS p, (SELECT publication_id FROM publication_author"
                       " WHERE author_id=%s) AS t1,"
                       "(SELECT id, name FROM author WHERE id=%s) AS t2  "
                       "WHERE t1.publication_id=p.id LIMIT %s, %s", [author_id, author_id, start, limit])
            authors = dictfetchall(cursor)

        elif limit > 100 or limit < 0:
            cursor.execute("SELECT t1.publication_id, t2.name,t2.id as a_id, title, abstract "
                       "FROM publication AS p, (SELECT publication_id FROM publication_author"
                       " WHERE author_id=%s) AS t1,"
                       "(SELECT id, name FROM author WHERE id=%s) AS t2  "
                       "WHERE t1.publication_id=p.id LIMIT 0, 10", [author_id, author_id])
            authors = dictfetchall(cursor)
        else:
            raise Http404("No such author_publication page")

        if authors:
            return render_to_response('author_publicaion.html', {'authors': authors,
                                                             'next_page': next_page,
                                                             'curr_page': page},
                              context_instance=RequestContext(request))
        else:
            raise Http404("No such author_publication page")
    except:
        raise Http404("Wrong parameters")