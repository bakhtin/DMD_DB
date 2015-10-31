from django.shortcuts import render_to_response, redirect
from django.template import RequestContext
from django.views.decorators.http import require_http_methods
from .forms import LoginForm
from user import *

@require_http_methods(['GET', 'POST'])
def do_authenticate(request):
    if request.method == 'GET':
        login_form = LoginForm()
        return render_to_response('login.html', {'login_form': login_form},
                              context_instance=RequestContext(request))
    else:
        session_id = authenticate(request.POST.get('login'), request.POST.get('password'))
        if session_id:
            response = redirect('/')
            response.set_cookie('session_id', value=session_id, max_age=86400)
            return response
        else:
            message = 'Bad credentials'
            login_form = LoginForm()
            return render_to_response('login.html', {'login_form': login_form,
                                                     'message': message},
                              context_instance=RequestContext(request))