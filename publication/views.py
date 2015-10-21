from django.views.decorators.http import require_http_methods
from django.db import connection

@require_http_methods(['GET'])
def show_publication_full(request, publication_id):
    if request.method == 'GET':
        cursor = connection.cursor()
        cursor.execute("SELECT abstract FROM publication WHERE id = %s", [publication_id])
        row = cursor.fetchone()
        return row

    return None