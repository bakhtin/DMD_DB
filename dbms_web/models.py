# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
#
# Also note: You'll have to insert the output of 'django-admin sqlcustom [app_label]'
# into your database.
from __future__ import unicode_literals

from django.db import models


class Affiliation(models.Model):
    name = models.CharField(unique=True, max_length=254)

    class Meta:
        managed = False
        db_table = 'affiliation'


class Author(models.Model):
    name = models.CharField(unique=True, max_length=254, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'author'


class IssueName(models.Model):
    name = models.CharField(unique=True, max_length=254)

    class Meta:
        managed = False
        db_table = 'issue_name'


class IssueType(models.Model):
    type = models.CharField(unique=True, max_length=254)

    class Meta:
        managed = False
        db_table = 'issue_type'


class Keyword(models.Model):
    word = models.CharField(unique=True, max_length=254)

    class Meta:
        managed = False
        db_table = 'keyword'


class Publication(models.Model):
    title = models.CharField(max_length=1023)
    issn = models.CharField(max_length=9, blank=True, null=True)
    isbn = models.CharField(max_length=13, blank=True, null=True)
    doi = models.CharField(unique=True, max_length=31, blank=True, null=True)
    pubdate = models.DateField(blank=True, null=True)
    pages = models.CharField(max_length=20, blank=True, null=True)
    volume = models.IntegerField(blank=True, null=True)
    abstract = models.TextField(blank=True, null=True)
    url = models.CharField(max_length=2083, blank=True, null=True)
    pub_number = models.CharField(max_length=45, blank=True, null=True)
    issue_name = models.ForeignKey(IssueName, blank=True, null=True)
    issue_type = models.ForeignKey(IssueType, blank=True, null=True)
    affiliation = models.ForeignKey(Affiliation, blank=True, null=True)
    publisher = models.ForeignKey('Publisher', blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'publication'


class PublicationAuthor(models.Model):
    publication = models.ForeignKey(Publication)
    author = models.ForeignKey(Author)

    class Meta:
        managed = False
        db_table = 'publication_author'
        unique_together = ['publication', 'author']


class PublicationKeyword(models.Model):
    keyword = models.ForeignKey(Keyword)
    publication = models.ForeignKey(Publication)
    type = models.CharField(max_length=17)

    class Meta:
        managed = False
        db_table = 'publication_keyword'
        unique_together = ['keyword', 'publication', 'type']


class Publisher(models.Model):
    name = models.CharField(unique=True, max_length=45)

    class Meta:
        managed = False
        db_table = 'publisher'
