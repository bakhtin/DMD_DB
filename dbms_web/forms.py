from django import forms


class SearchForm(forms.Form):
    operator = forms.ChoiceField(choices=[(x.lower(), x) for x in ['AND', 'OR', 'NOT']])
    search_field = forms.CharField(min_length=3, max_length=1024)
    criteria = forms.ChoiceField(choices=[(x.lower(), x) for x in ['Title', 'Author', 'Keyword', 'Issue name',
                                                                   'Affiliation', 'Publication year']])
    match = forms.ChoiceField(choices=[(x.lower(), x) for x in ['EXACT', 'LIKE']])