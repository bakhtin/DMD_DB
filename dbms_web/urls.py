"""dmd_project URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.8/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Add an import:  from blog import urls as blog_urls
    2. Add a URL to urlpatterns:  url(r'^blog/', include(blog_urls))
"""
from django.conf.urls import include, url
from django.contrib import admin
import views, publication.views, author.views, manager.views

urlpatterns = [
    url(r'^$', publication.views.search_publication),
    url(r'^admin/', include(admin.site.urls)),
    url(r'^search/', views.search_pub),
    url(r'^publication/publication_id/([0-9]+)', publication.views.publication_full, name='show_publication_detail'),
    url(r'^author/author_id/([0-9]+)/page/([0-9]+)/limit/([0-9]+)', author.views.related_articles),
    url(r'^author/author_id/([0-9]+)', author.views.related_articles, {'page': 1, 'limit': 10},
        name='show_author_page_base'),
    url(r'^login/', manager.views.do_authenticate, name='authentication'),
    url(r'^publication/add/', publication.views.publication_add, name='publication_add'),
    url(r'^publication/edit/([0-9]+)/', publication.views.publication_edit, name='publication_edit'),
    url(r'^publication/delete/', publication.views.publication_delete, name='publication_delete'),


]
