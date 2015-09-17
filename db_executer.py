__author__ = 'Artyom Bakhtin'

from mysql.connector import connection


def add_publisher(cursor, name):
    query = ('insert into publisher(name) values ("%s")' % name)
    cursor.execute(query)
    return cursor.lastrowid


def add_affiliation(cursor, name):
    query = ('insert into affiliation(name) values ("%s")' % name)
    cursor.execute(query)
    return cursor.lastrowid


def add_issue_name(cursor, name):
    query = ('insert into issue_name(name) values ("%s")' % name)
    cursor.execute(query)
    return cursor.lastrowid


def add_issue_type(cursor, type):
    query = ('insert into issue_type(type) values ("%s")' % type)
    cursor.execute(query)
    return cursor.lastrowid


def add_keyword(cursor, type, word):
    query = ('insert into keyword(type, word) values ("%s", "%s")' % (type, word))
    cursor.execute(query)
    return cursor.lastrowid


def add_author(cursor, name):
    query = ('insert into author(name) values ("%s")' % name)
    cursor.execute(query)
    return cursor.lastrowid


def add_publication_author(cursor, publication_id, author_id):
    query = ('insert into publication_author(publication_id, author_id) values ("%s", "%s")' % (publication_id,
                                                                                                author_id))
    cursor.execute(query)
    return cursor.lastrowid


def add_publication_keyword(cursor, publication_id, keyword_id):
    query = ('insert into publication_keyword(publication_id, keyword_id) values ("%s", "%s")' % (publication_id,
                                                                                                keyword_id))
    cursor.execute(query)
    return cursor.lastrowid

def add_publication(cursor, title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number,
                   issue_name_id, issue_type_id, affiliation_id, publisher_id):
    query = ('insert into publication(title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number, '
             'issue_name_id, issue_type_id, affiliation_id, publisher_id) '
             'values ("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s")' % (title, issn, isbn, doi, pubdate,
                                                                                    pages, volume, abstract, url,
                                                                                    pub_number, issue_name_id,
                                                                                    issue_type_id, affiliation_id,
                                                                                    publisher_id))
    cursor.execute(query)
    return cursor.lastrowid


def parse_and_execute(**kwargs):
    '''
    dict expected ({'param_name': 'param_value1'}) -- for 1:n, 1:1 records;
    dict expected ({'param_name': ('param_value1', 'param_value2')}) -- for n:m records;
    :param cursor
    :param publisher_name:
    :param affiliation_name:
    :param issue_name_name:
    :param issue_type_type:
    :param keywords[]:
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
    :param publication_pubnumber
    :return: last_publication_id
    '''

    last_keyword_ids = []
    last_author_ids = []

    last_publisher_id = add_publisher(cursor, kwargs['publisher_name'])
    last_affiliation_id = add_affiliation(cursor, kwargs['affiliation_name'])
    last_issue_name_id = add_issue_name(cursor, kwargs['issue_name_name'])
    last_issue_type_id = add_issue_name(cursor, kwargs['issue_type_type'])
    for key in kwargs['keywords'].items:
        for value in key:
            last_keyword_ids.append(add_keyword(cursor, key, value))
    for key in kwargs['authors'].items:
        for value in key:
            last_author_ids.append(add_author(cursor, value))
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
    for author_id in last_author_ids:
        add_publication_author(cursor, last_publication_id, author_id)
    for keyword_id in last_keyword_ids:
        add_publication_keyword(cursor, last_publication_id, keyword_id)

    return last_publication_id

cnx = connection.MySQLConnection(user='dmd', password='500000',
                                 host='127.0.0.1',
                                 database='dmd_db')
cursor = cnx.cursor()
# parse_and_execute() loop here
    #cnx.commit() after each iteration

cnx.close()