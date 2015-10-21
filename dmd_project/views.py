from django.shortcuts import render_to_response
from django.template import RequestContext
from django.views.decorators.http import require_http_methods
from forms import SearchForm

@require_http_methods(['GET', 'POST'])
def search_pub(request):
    if request.method == 'POST':
        pass
    else:
        search_form = SearchForm()
    return render_to_response('search_form.html', {'search_form': search_form},
                              context_instance=RequestContext(request))