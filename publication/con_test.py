from django.db import connection



def dictfetchall(cursor):
    # Return all rows from a cursor as a dict
    columns = [col[0] for col in cursor.description]
    return [
        dict(zip(columns, row))
        for row in cursor.fetchall()
    ]


cursor = connection.cursor()
cursor.execute("describe publication_author")
a = dictfetchall(cursor)
print a
