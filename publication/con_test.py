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


def query_builder(**kwargs):
    # assuming number of elements in each pair equal -> else - FAIL
    from_tables = set()
    attrs = []
    join_tables = set()
    cross_join_tables = set()

    query_parameters = map(dict, zip(*[[(k, v) for v in value] for k, value in kwargs.items()]))  # Python <3
    for k in query_parameters:
        if k['criteria'] == 'author':
            from_tables.add('author')
            from_tables.add('publication_author')
            if k['operator'] in ['AND', 'OR']:
                if attrs:
                    attrs.append('%s author.name="%s"' % (k['operator'], k['search_field']))
                else:
                    attrs.append('author.name="%s"' % k['search_field'])
            else:
                if attrs:
                    attrs.append('author.name!="%s"' % k['search_field'])
                else:
                    attrs.append('AND author.name!="%s"' % k['search_field'])
            join_tables.add(' AND author.id=publication_author.author_id')
            cross_join_tables.add('publication_author.publication_id')

        elif k['criteria'] == 'keyword':
            from_tables.add('keyword')
            from_tables.add('publication_keyword')
            if k['operator'] in ['AND', 'OR']:
                if attrs:
                    attrs.append('%s keyword.word="%s"' % (k['operator'], k['search_field']))
                else:
                    attrs.append('keyword.word="%s"' % k['search_field'])
            else:
                if attrs:
                    attrs.append('keyword.word!="%s"' % k['search_field'])
                else:
                    attrs.append('AND keyword.word!="%s"' % k['search_field'])
            join_tables.add('AND keyword.id=publication_keyword.keyword_id')
            cross_join_tables.add('publication_keyword.publication_id')

        elif k['criteria'] == 'issue_name':
            from_tables.add('issue_name')
            from_tables.add('publication')
            if k['operator'] in ['AND', 'OR']:
                if attrs:
                    attrs.append('%s issue_name.name="%s"' % (k['operator'], k['search_field']))
                else:
                    attrs.append('issue_name.name="%s"' % k['search_field'])
            else:
                if attrs:
                    attrs.append('AND issue_name.name!="%s"' % k['search_field'])
                else:
                    attrs.append('issue_name.name!="%s"' % k['search_field'])
            join_tables.add('AND issue_name.id=publication.issue_name_id')
            cross_join_tables.add('publication.id')

        elif k['criteria'] == 'issue_type':
            from_tables.add('issue_type')
            from_tables.add('publication')
            if k['operator'] in ['AND', 'OR']:
                if attrs:
                    attrs.append('%s issue_type.type="%s"' % (k['operator'], k['search_field']))
                else:
                    attrs.append('issue_type.type="%s"' % k['search_field'])
            else:
                if attrs:
                    attrs.append('AND issue_type.type!="%s"' % k['search_field'])
                else:
                    attrs.append('issue_type.type!="%s"' % k['search_field'])
            join_tables.add('AND issue_type.id=publication.issue_type_id')
            cross_join_tables.add('publication.id')

        elif k['criteria'] == 'affiliation':
            from_tables.add('affiliation')
            from_tables.add('publication')
            if k['operator'] in ['AND', 'OR']:
                if attrs:
                    attrs.append('%s affiliation.name="%s"' % (k['operator'], k['search_field']))
                else:
                    attrs.append('affiliation.name="%s"' % k['search_field'])
            else:
                if attrs:
                    attrs.append('AND affiliation.name!="%s"' % k['search_field'])
                else:
                    attrs.append('affiliation.name!="%s"' % k['search_field'])
            join_tables.add('AND affiliation.id=publication.affiliation_id')
            cross_join_tables.add('publication.id')

        elif k['criteria'] == 'title':
            from_tables.add('publication')
            if k['operator'] in ['AND', 'OR']:
                if attrs:
                    attrs.append('%s publication.title="%s"' % (k['operator'], k['search_field']))
                else:
                    attrs.append('publication.title="%s"' % k['search_field'])
            else:
                if attrs:
                    attrs.append('AND publication.title!="%s"' % k['search_field'])
                else:
                    attrs.append('publication.title!="%s"' % k['search_field'])
            cross_join_tables.add('publication.id')

        elif k['criteria'] == 'pub_date':
            from_tables.add('publication')
            if k['operator'] in ['AND', 'OR']:
                if attrs:
                    attrs.append('%s publication.pub_date="%s"' % (k['operator'], k['search_field']))
                else:
                    attrs.append('publication.pub_date="%s"' % k['search_field'])
            else:
                if attrs:
                    attrs.append('AND publication.pub_date!="%s"' % k['search_field'])
                else:
                    attrs.append('publication.pub_date!="%s"' % k['search_field'])
            cross_join_tables.add('publication.id')


    cross_join_tables_string = ''
    for x in combinations(cross_join_tables, 2):
        cross_join_tables_string += ' AND %s=%s ' % (x[0], x[1])
    if 'author' not in from_tables and 'keyword' not in from_tables:
        base_part = 'SELECT publication.id FROM '
    else:
        if 'author' in from_tables:
            base_part = 'SELECT publication_author.publication_id FROM '
        else:
            base_part = 'SELECT publication_keyword.publication_id FROM '

    attrs_string = ''
    for i, x in enumerate(attrs):
        if i > 0:
            attrs_string = '(' + attrs_string + x + ') '
        else:
            attrs_string = x + ' '
    f_query = base_part + ', '.join([x for x in from_tables]) + ' WHERE ' + attrs_string + ' '.join(
        [x for x in join_tables]) + cross_join_tables_string
    #return [from_tables, attrs, join_tables, cross_join_tables_string]
    return f_query


dic = {'operator': ['AND', 'OR'],
       'criteria': ['author', 'author'],
       'match': ['exact', 'exact'],
       'search_field': ['Abdat S.', 'Abdelaziz A.']}
a = query_builder(**dic)
print a