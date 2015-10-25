from django.db import connection
from django.contrib.auth import hashers as hashers
from Crypto import Random
import base64
import datetime


def dictfetchall(cursor):
    # Return all rows from a cursor as a dict
    columns = [col[0] for col in cursor.description]
    return [
        dict(zip(columns, row))
        for row in cursor.fetchall()
    ]


def authenticate(user_login, password):
    expire_time = datetime.datetime.now() + datetime.timedelta(0, 86400)
    cursor = connection.cursor()
    cursor.execute("select id, login, password from user where login=%s", [user_login])
    a = dictfetchall(cursor)
    if a:
        try:
            a = a[0]
            if hashers.check_password(str(password), a['password']):
                user_id = a['id']
                a = Random.new()
                session_id = base64.b64encode(hashers.make_password(a.read(32), hasher='unsalted_md5') + ':' + str(user_id))

                cursor.execute("insert into session(session_id, user_id, expire_time) values(%s, %s, %s)"
                               "ON DUPLICATE KEY UPDATE session_id=%s;", [session_id, user_id, expire_time,
                                                                          session_id])
                # return session_id string to set user's cookie
                return session_id
        except:
            return False
    else:
        return False


def deauthenticate(session_id):
    try:
        cursor = connection.cursor()
        cursor.execute("delete * from session where session_id=%s", [session_id])
        return None
    except:
        return None


def is_authenticated(session_id):
    try:
        cookie = base64.b64decode(session_id).split(":")
        session_id = cookie[0]
        user_id = int(cookie[1])
        cursor = connection.cursor()
        cursor.execute("select * from session where user_id=%s", [user_id])
        a = dictfetchall(cursor)[0]
        if a:
            if base64.b64decode(a['session_id']).split(':')[0] == session_id \
                    and a['user_id'] == user_id\
                    and datetime.datetime.now() < a['expire_time']:
                return True
            else:
                return False
    except:
        return False