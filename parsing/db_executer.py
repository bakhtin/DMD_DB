__author__ = 'Artyom Bakhtin'


def add_quotes_if_not_null(arg):
    return arg if arg == 'NULL' else '\'%s\'' % arg


def add_publication(cursor, title, issn, isbn, doi, pubdate, pages,
                    volume, abstract, url, pub_number,
                    issue_name_id, issue_type_id, affiliation_id, publisher_id):
    title = add_quotes_if_not_null(title)
    issn = add_quotes_if_not_null(issn)
    isbn = add_quotes_if_not_null(isbn)
    doi = add_quotes_if_not_null(doi)
    pubdate = add_quotes_if_not_null(pubdate)
    pages = add_quotes_if_not_null(pages)
    volume = add_quotes_if_not_null(volume)
    abstract = add_quotes_if_not_null(abstract)
    url = add_quotes_if_not_null(url)
    pub_number = add_quotes_if_not_null(pub_number)
    issue_name_id = add_quotes_if_not_null(issue_name_id)
    issue_type_id = add_quotes_if_not_null(issue_type_id)
    affiliation_id = add_quotes_if_not_null(affiliation_id)
    publisher_id = add_quotes_if_not_null(publisher_id)

    query = ('insert into publication(title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number, '
             'issue_name_id, issue_type_id, affiliation_id, publisher_id) '
             'values (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)' % (
                 title, issn, isbn, doi, pubdate,
                 pages, volume, abstract, url,
                 pub_number, issue_name_id,
                 issue_type_id, affiliation_id,
                 publisher_id))
    cursor.execute(query)
    return cursor.lastrowid


def insert_unique(cursor, table, attrib, thing, id="id"):
    if thing == 'NULL':
        # insert null? no. skip this
        return 'NULL'

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


# @profile
def parse_and_execute(cursor, pub):
    '''
    :param cursor
    :param publication
    :return: last_publication_id
    '''

    # process keywords
    kw_ids = {}  # key - keyword type, value - list of keywords ids
    if pub.keywords is not None and pub.keywords != 'NULL':
        for kwtype in pub.keywords:  # for each keyword type
            for kw in pub.keywords[kwtype]:  # for each keyword of specified type
                if kwtype not in kw_ids:
                    kw_ids[kwtype] = set()
                else:
                    kw_ids[kwtype].add(insert_unique(cursor, "keyword", "word", kw))

    # process authors
    authors_ids = set()
    if pub.authors is not None and pub.authors != 'NULL':
        for name in pub.authors:
            authors_ids.add(insert_unique(cursor, "author", "name", name))

    last_publisher_id = insert_unique(cursor, "publisher", "name", pub.publisher)
    last_affiliation_id = insert_unique(cursor, "affiliation", "name", pub.affiliation)
    last_issue_name_id = insert_unique(cursor, "issue_name", "name", pub.issue_name)
    last_issue_type_id = insert_unique(cursor, "issue_type", "type", pub.issue_type)

    # process pubication date
    year = pub.pubdate[0]
    if len(pub.pubdate) == 2:
        month = pub.pubdate[1]
        day = '01'
    else:
        month = '01'
        day = '02'
    pubdate = '%s-%s-%s' % (year, month, day)

    # process publication
    last_publication_id = add_publication(cursor,
                                          pub.title,
                                          pub.issn,
                                          pub.isbn,
                                          pub.doi,
                                          pubdate,
                                          pub.pages,
                                          pub.volume,
                                          pub.abstract,
                                          pub.url,
                                          pub.pubnumber,
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
        cursor.execute(query)

    return last_publication_id
