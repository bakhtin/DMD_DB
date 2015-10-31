from django import forms


class PubAdditionForm(forms.Form):
    title = forms.CharField(max_length=1023, required=True)
    issn = forms.CharField(max_length=9, required=False)
    isbn = forms.CharField(max_length=13, required=False)
    doi = forms.CharField(max_length=31, required=True)
    pubdate = forms.DateField(required=False)
    pages = forms.CharField(max_length=20, required=False)
    volume = forms.IntegerField(required=False)
    abstract = forms.CharField(widget=forms.Textarea, required=False)
    url = forms.URLField(required=False)
    pub_number = forms.CharField(max_length=45, required=False)
    issue_name = forms.CharField(max_length=512, required=True)
    issue_type = forms.CharField(max_length=254, required=True)
    affiliation = forms.CharField(max_length=512, required=True)
    publisher = forms.CharField(max_length=45, required=True)
    authors = forms.CharField(widget=forms.Textarea, required=True)
    thesaurus_terms_keywords = forms.CharField(widget=forms.Textarea, required=True)
    controlled_terms_keywords = forms.CharField(widget=forms.Textarea, required=True)
    uncontrolled_terms_keywords = forms.CharField(widget=forms.Textarea, required=True)


class SearchForm(forms.Form):
    operator = forms.ChoiceField(choices=[(x.lower(), x) for x in ['AND', 'OR', 'NOT']])
    search_field = forms.CharField(min_length=3, max_length=1024)
    criteria = forms.ChoiceField(choices=[(x.lower(), x) for x in ['Title', 'Author', 'Keyword', 'Issue name',
                                                                   'Affiliation', 'Publication year']])
    match = forms.ChoiceField(choices=[(x.lower(), x) for x in ['EXACT', 'LIKE']])