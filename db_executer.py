__author__ = 'Artyom Bakhtin'


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


def add_keyword_word(cursor, word):
    query = ('insert into keyword_word(word) values ("%s")' % word)
    cursor.execute(query)
    return cursor.lastrowid


def add_keyword_type(cursor, type):
    query = ('insert into keyword_type(type) values ("%s")' % type)
    cursor.execute(query)
    return cursor.lastrowid


def add_keyword_word_type(cursor, word_id, type_id, publication_id):
    query = ('insert into keyword_word_type(keyword_word_id, keyword_type_id, publication_id) '
             'values ("%s", "%s", "%s")' % (word_id, type_id, publication_id))
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


def add_publication(cursor, title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number,
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

    last_keyword_ids = {}
    last_author_ids = []

    last_publisher_id = add_publisher(kwargs['cursor'], kwargs['publisher_name'])
    last_affiliation_id = add_affiliation(kwargs['cursor'], kwargs['affiliation_name'])
    last_issue_name_id = add_issue_name(kwargs['cursor'], kwargs['issue_name_name'])
    last_issue_type_id = add_issue_type(kwargs['cursor'], kwargs['issue_type_type'])
    if kwargs['keywords'] != 'NULL':
        for key in kwargs['keywords']:
            try:
                prev_id = add_keyword_type(kwargs['cursor'], key)
                last_keyword_ids[prev_id] = []
            except Exception:
                query = ('select id from keyword_type where type="%s"' % key)
                prev_id = kwargs['cursor'].execute(query)
                last_keyword_ids[prev_id] = []
            for word in kwargs['keywords'][key]:
                try:
                    last_keyword_ids[prev_id].append((add_keyword_word(kwargs['cursor'], word), word))
                except Exception:
                    continue
    if kwargs['authors'] != 'NULL':
        for author in kwargs['authors']:
            try:
                last_author_ids.append(add_author(kwargs['cursor'], author))
            except Exception:
                query = ('select id from author where name="%s"' % author)
                last_author_ids.append(kwargs['cursor'].execute(query))



    year = kwargs['publication_pubdate'][0]
    day = '01'
    if len(kwargs['publication_pubdate']) > 1:
        month = kwargs['publication_pubdate'][1]
        #month = '05'
    else:
        month = '01'
    kwargs['publication_pubdate'] = '%s-%s-%s' % (year, month, day)

    last_publication_id = add_publication(kwargs['cursor'],
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
    if last_author_ids:
        for author_id in last_author_ids:
            add_publication_author(kwargs['cursor'], last_publication_id, author_id)
    for type_id in last_keyword_ids.items():
        for keyword_id in type_id[1]:
            add_keyword_word_type(kwargs['cursor'], keyword_id[0], type_id[0], last_publication_id)

    try:
        kwargs['cursor'].fetchall()
    except:
        pass

    return last_publication_id