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


cursor = connection.cursor(raw=True)
cursor.execute("select id, pubdate from publication where id=2")
a = cursor.fetchone(raw=True)
print a