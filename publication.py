from pprint import pprint

__author__ = 'bogdan'


# represents single publication
class Publication:
    document = None
    # fields:
    title = ""
    authors = []
    affiliations = ""
    abstract = ""
    pubtitle = ""
    pubnumber = 0
    issn = ""
    isbn = ""
    doi = ""
    pubdate = ""
    pages = ""
    volume = 0
    issue_name = ""
    issue_type = ""
    publisher = ""
    keywords = {}

    # parse tag with attribute='id'
    # 'father' is an attribute name of father attribute
    # example: <author>I am author</author>, attribute='author'
    def get(self, attribute, father=None):
        if father is None:
            text = self.document.findall(attribute)
            try:
                text = text[0].text
                return 'NULL' if text == 'None' else text
            except IndexError as e:
                return 'NULL'

        else:
            try:
                text = self.document.findall(father)
                text = text[0].findall(attribute)
                text = [x.text for x in text if x != 'None']
                return text
            except IndexError as e:
                return 'NULL'

    # xml_document is a Element of xml.etree.ElementTree with all attributes
    def __init__(self, xml_document):
        self.document = xml_document

        self.title = self.get('title')
        self.authors = self.parse_authors()
        self.affiliation = self.get('affiliations')
        self.abstract = self.get('abstract')
        self.issn = self.get('issn')
        self.isbn = self.get('isbn')
        self.doi = self.get('doi')
        self.url = self.get('pdf')
        self.volume = self.get('volume')
        self.publisher = self.get('publisher')
        self.issue_name = self.get('pubtitle')
        self.issue_type = self.get('pubtype')
        self.pages = self.get('spage') + "-" + self.get('epage')
        self.pubdate = self.parse_pubdate()
        self.keywords = self.parse_keywords()
        self.pubnumber = self.get('punumber')

    # returns a string with format: month-year
    def parse_pubdate(self):
        month = self.get('issue')
        pubd = 0
        if month != 'NULL':
            pubd = int(month)
        year = self.get('py')
        if year != 'NULL':
            return [int(year), int(pubd)]
        else:
            return [int(year)]

    # returns a dict with
    # keys: controlledterms, uncontrolledterms, thesaurusterms
    # values are lists of keywords
    def parse_keywords(self):
        keys = ['controlledterms', 'uncontrolledterms', 'thesaurusterms']
        keywords = {}

        for k in keys:
            kw = self.get('term', k)
            if len(kw) == 0 or kw == 'NULL':
                continue
            else:
                keywords[k] = kw

        return keywords

    # returns list of authors or NULL
    def parse_authors(self):
        str_authors = self.get('authors')
        if str_authors == "NULL" or str_authors is None:
            return "NULL"

        authors = str_authors.split("; ")
        if len(authors) == 0:
            return 'NULL'
        else:
            authors = tuple([x.strip().replace(',', '') for x in authors])

        return authors

    # string representation of publication
    # use case:
    # pub = Publication(xml) # xml is Element<document>
    # print(pub)
    def __str__(self):
        return "title: \t\t\t" + str(self.title) + "\n" + \
               "authors: \t\t" + str(self.authors) + "\n" + \
               "affiliation: \t" + str(self.affiliation) + "\n" + \
               "issue_name: \t" + str(self.issue_name) + "\n" + \
               "issue_type: \t" + str(self.issue_type) + "\n" + \
               "issn: \t\t\t" + str(self.issn) + "\n" + \
               "isbn: \t\t\t" + str(self.isbn) + "\n" + \
               "abstract: \t\t" + str(self.abstract) + "\n" + \
               "doi: \t\t\t" + str(self.doi) + "\n" + \
               "url: \t\t\t" + str(self.url) + "\n" + \
               "publisher: \t\t" + str(self.publisher) + "\n" + \
               "pages: \t\t\t" + str(self.pages) + "\n" + \
               "pubdate: \t\t" + str(self.pubdate) + "\n" + \
               "keywords: \t\t" + str(self.keywords) + "\n"
