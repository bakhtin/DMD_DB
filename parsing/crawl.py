# -*- coding: utf-8 -*-
import grequests

fromid = 1650000
toid   = 2000000
partsize = 300


for i in xrange(fromid, toid, partsize ):
    print "Current id: %i" % (i)
    urls = []
    for j in range(i, i + partsize):
        urls.append('http://ieeexplore.ieee.org/gateway/ipsSearch.jsp?an=%s' % str(j))
    
    # urls
    rs = (grequests.get(u) for u in urls)
    rs = grequests.map(rs)


    with open('./files/data_part_%s.xml' % str(i), 'w') as f:
        for response in rs:
            try:
                text = response.text
                f.write(text.encode('utf-8'))
            except:
                print "Error at ", i

        f.close()
