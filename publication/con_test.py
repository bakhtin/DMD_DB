from django.db import connection
from dbms_web.custom_exception import *
from itertools import combinations


def dictfetchall(cursor):
    # Return all rows from a cursor as a dict
    columns = [col[0] for col in cursor.description]
    return [
        dict(zip(columns, row))
        for row in cursor.fetchall()
    ]


cursor = connection.cursor()
publication_id = 1053
cursor.execute(
    "SELECT title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number "
    "FROM publication "
    "WHERE id = %s ", [publication_id])  # publication = dictfetchall(cursor)[0]
publication = cursor.fetchall()
print publication