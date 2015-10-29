class NotAuthenticatedException(Exception):
    def __init__(self):
        self.text = 'Authentication required'
        self.code = '10'

    def __str__(self):
        return '%s. Code %s.' % (self.text, self.code)

class BadCredentials(Exception):
    def __init__(self):
        self.text = 'username/password wrong'
        self.code = '11'

    def __str__(self):
        return '%s. Code %s.' % (self.text, self.code)