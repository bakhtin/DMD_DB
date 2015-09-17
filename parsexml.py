from os import listdir
from pprint import pprint
import xml.etree.ElementTree as ET
import time
from publication import Publication

folder = "./files/"
files = [folder + f for f in listdir(folder)]

if __name__ == '__main__':
    count = 0

    start_time = time.time()

    # for each file in the folder
    for path in files:
        print(path)
        #if count >= 1:
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
                    if pub.title == 'NULL':
                        print(pub.url)
                        exit()

                    #print(pub)

                    count += 1


            except ET.ParseError as e:
                pass

    print(" %i unique records " % (count))
    print(" --- %s seconds --- " % (time.time() - start_time))
