from os import listdir
import xml.etree.ElementTree as ET
import time
from publication import Publication
import db_executer
from mysql.connector import connection

cnx = connection.MySQLConnection(user='dmd', password='500000',
                                 host='10.91.43.146',
                                 database='dmd_db')
cursor = cnx.cursor()

folder = "/media/bogdan/archive/Git/CRAWL/files1/"
files = [folder + f for f in listdir(folder)]

if __name__ == '__main__':
    count = 0

    start_time = time.time()

    # for each file in the folder
    for path in files:
        print(path)
        # if count >= 1:
        #    break

        # open it
        file = open(path, 'r')
        # read in buffer
        data = file.read()
        # split buffer in separate quieries
        data = data.split('<?xml version="1.0" encoding="UTF-8"?>')

        # for each query in the buffer
        for id in data:
            # restore the header
            id = '<?xml version="1.0" encoding="UTF-8"?>' + id
            try:
                # parse xml tree
                content = ET.fromstring(id)
                # if we have a good query, proceed it
                if content.tag == 'root':
                    content = content[2]  # get document
                    pub = Publication(content)
                    print db_executer.parse_and_execute(cursor=cursor,
                                                        publisher_name=pub.publisher,
                                                        affiliation_name=pub.affiliation,
                                                        issue_name_name=pub.issue_name,
                                                        issue_type_type=pub.issue_type,
                                                        keywords=pub.keywords,
                                                        authors=pub.authors,
                                                        publication_title=pub.title,
                                                        publication_issn=pub.issn,
                                                        publication_isbn=pub.isbn,
                                                        publication_doi=pub.doi,
                                                        publication_pubdate=pub.pubdate,
                                                        publication_pages=pub.pages,
                                                        publication_volume=pub.volume,
                                                        publication_abstract=pub.abstract,
                                                        publication_url=pub.url,
                                                        publication_pubnumber=pub.pubnumber)
                    cnx.commit()
                    count += 1


            except Exception as e:
                print e
                continue
    cnx.close()
    print(" %i unique records " % (count))
    print(" --- %s seconds --- " % (time.time() - start_time))
