from django.db import connection
from django.contrib.auth import hashers as hashers
from Crypto import Random
import base64
import datetime
from django.utils import timezone
from dbms_web.custom_exception import *

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

                cursor.execute("select user_id from session WHERE user_id=%s", [user_id])
                if len(cursor.fetchall()) == 0:
                    cursor.execute("insert into session(session_id, user_id, expire_time) values(%s, %s, %s)",
                                   [session_id, user_id, expire_time])
                else:
                    cursor.execute("update session set session_id=%s, expire_time=%s", [session_id, expire_time])
                    # return session_id string to set user's cookie
                return session_id
        except:
            raise BadCredentials
    else:
        raise BadCredentials


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
                    and timezone.now() < a['expire_time']:
                return True
            else:
                raise NotAuthenticatedException
    except:
        raise NotAuthenticatedException


def check_role(session_id):
    try:
        cookie = base64.b64decode(session_id).split(":")
        user_id = int(cookie[1])
        cursor = connection.cursor()
        cursor.execute("select role from user_role, role where user_id=%s and user_role.role_id=role.id", [user_id])
        a = dictfetchall(cursor)[0]
        if a:
            return a['role']
        else:
            return None
    except:
        return None