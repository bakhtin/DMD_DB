__author__ = 'Artyom Bakhtin'


def add_publication(cursor, title, issn, isbn, doi, pubdate, pages,
                    volume, abstract, url, pub_number,
                    issue_name_id, issue_type_id, affiliation_id, publisher_id):
    query = ('insert into publication(title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number, '
             'issue_name_id, issue_type_id, affiliation_id, publisher_id) '
             'values ("%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s")' % (
                 title, issn, isbn, doi, pubdate,
                 pages, volume, abstract, url,
                 pub_number, issue_name_id,
                 issue_type_id, affiliation_id,
                 publisher_id))
    cursor.execute(query)
    return cursor.lastrowid


def insert_unique(cursor, table, attrib, thing, id="id"):
    sel_query = ('select %s from %s where %s=\'%s\'') % (id, table, attrib, thing)
    ins_query = ('insert into %s(%s) values (\'%s\')') % (table, attrib, thing)

    cursor.execute(sel_query)
    row = cursor.fetchone()
    if row is None:
        try:
            cursor.execute(ins_query)
            return cursor.lastrowid
        except Exception as e:
            print "something bad happened: %s;\t%s" % (ins_query, e)
    else:
        return row[0]


def parse_and_execute(**kwargs):
    '''
    dict expected({'param_name': 'param_value1'}) - -
    for 1: n, 1:1 records;
    dict expected({'param_name': ('param_value1', 'param_value2')}) - -
    for n: m records;
    :param cursor
    :param publisher_name:
    :param affiliation_name:
    :param issue_name_name:
    :param issue_type_type:
    :param keywords{}:
    :param authors[]:
    :param publication_title
    :param publication_issn
    :param publication_isbn
    :param publication_doi
    :param publication_pubdate
    :param publication_pages
    :param publication_volume
    :param publication_abstract
    :param publication_url
    :parampublication_pubnumber
    :return: last_publication_id
    '''

    cursor = kwargs['cursor']

    # process keywords
    kws = kwargs['keywords']
    kw_ids = {}  # key - keyword type, value - list of keywords ids
    if kws is not None and kws != 'NULL':
        for kwtype in kws:  # for each keyword type
            for kw in kws[kwtype]:  # for each keyword of specified type
                if kwtype not in kw_ids:
                    kw_ids[kwtype] = set()
                else:
                    kw_ids[kwtype].add(insert_unique(cursor, "keyword", "word", kw))

    # process authors
    authors = kwargs['authors']
    authors_ids = []
    if authors is not None and authors != 'NULL':
        for name in authors:
            authors_ids.append(insert_unique(cursor, "author", "name", name))

    last_publisher_id = insert_unique(cursor, "publisher", "name", kwargs['publisher_name'])
    last_affiliation_id = insert_unique(cursor, "affiliation", "name", kwargs['affiliation_name'])
    last_issue_name_id = insert_unique(cursor, "issue_name", "name", kwargs['issue_name_name'])
    last_issue_type_id = insert_unique(cursor, "issue_type", "type", kwargs['issue_type_type'])

    # process pubication date
    year = kwargs['publication_pubdate'][0]
    if len(kwargs['publication_pubdate']) == 2:
        month = kwargs['publication_pubdate'][1]
        day = '01'
    else:
        month = '01'
        day = '02'
    kwargs['publication_pubdate'] = '%s-%s-%s' % (year, month, day)

    # process publication
    last_publication_id = add_publication(cursor,
                                          kwargs['publication_title'],
                                          kwargs['publication_issn'],
                                          kwargs['publication_isbn'],
                                          kwargs['publication_doi'],
                                          kwargs['publication_pubdate'],
                                          kwargs['publication_pages'],
                                          kwargs['publication_volume'],
                                          kwargs['publication_abstract'],
                                          kwargs['publication_url'],
                                          kwargs['publication_pubnumber'],
                                          last_issue_name_id,
                                          last_issue_type_id,
                                          last_affiliation_id,
                                          last_publisher_id)

    # process publication-keyword
    for kwtype in kw_ids:  # for each type
        for kwid in kw_ids[kwtype]:
            query = ('insert into publication_keyword(keyword_id, publication_id, type) values ("%s", "%s", "%s")' % (
                kwid, last_publication_id, kwtype))
            cursor.execute(query)

    # process publication-author
    for a_id in authors_ids:
        query = (
            'insert into publication_author(publication_id, author_id) values ("%s", "%s")' % (
                last_publication_id, a_id))

    return last_publication_id
