from os import listdir
import xml.etree.ElementTree as ET
import time
from mysql.connector import connection

from publication import Publication
import db_executer

cnx = connection.MySQLConnection(user='dmd_test', password='500000',
                                 host='10.91.43.146',
                                 database='dmd_db_test')
cursor = cnx.cursor()

folder = "/media/bogdan/archive/Git/CRAWL/files/"
files = [folder + f for f in listdir(folder)]


# @profile
def main():
    count = 0

    # for each file in the folder
    for path in files:

        start_time = time.time()

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
            try:
                # parse xml tree
                content = ET.fromstring(id)
                # if we have a good query, proceed it
                if content.tag == 'root':
                    content = content[2]  # get document
                    pub = Publication(content)
                    print db_executer.parse_and_execute(cursor, pub)
                    count += 1

            except Exception as e:
                print e
                continue
        cnx.commit()

        print("--- %i seconds elapsed --- " % (time.time() - start_time))

    cnx.close()
    print(" %i unique records " % (count))


if __name__ == '__main__':
    main()
