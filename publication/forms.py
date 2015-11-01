from django import forms


class PubAdditionForm(forms.Form):
    title = forms.CharField(widget=forms.TextInput(attrs={'class': 'form-control',
                                                          'placeholder': 'Title'}), max_length=1023, required=True)
    issn = forms.CharField(widget=forms.TextInput(attrs={'class': 'form-control',
                                                         'placeholder': 'ISSN'}), max_length=9, required=False)
    isbn = forms.CharField(widget=forms.TextInput(attrs={'class': 'form-control',
                                                         'placeholder': 'ISBN'}), max_length=13, required=False)
    doi = forms.CharField(widget=forms.TextInput(attrs={'class': 'form-control',
                                                        'placeholder': 'DOI'}), max_length=31, required=True)
    pubdate = forms.DateField(widget=forms.DateInput(attrs={'class': 'form-control',
                                                            'placeholder': 'Publication date'}), required=False)
    pages = forms.CharField(widget=forms.TextInput(attrs={'class': 'form-control',
                                                          'placeholder': 'Pages'}), max_length=20, required=False)
    volume = forms.IntegerField(widget=forms.TextInput(attrs={'class': 'form-control',
                                                              'placeholder': 'Publication voume'}), required=False)
    abstract = forms.CharField(widget=forms.Textarea(attrs={'class': 'form-control',
                                                            'placeholder': 'Abstract'}), required=False)
    url = forms.URLField(widget=forms.URLInput(attrs={'class': 'form-control',
                                                      'placeholder': 'URL'}), required=False)
    pub_number = forms.CharField(widget=forms.TextInput(attrs={'class': 'form-control',
                                                               'placeholder': 'Publication number'}), max_length=45,
                                 required=False)
    issue_name = forms.CharField(widget=forms.TextInput(attrs={'class': 'form-control',
                                                               'placeholder': 'Issue name'}), max_length=512,
                                 required=True)
    issue_type = forms.CharField(widget=forms.TextInput(attrs={'class': 'form-control',
                                                               'placeholder': 'Issue type'}), max_length=254,
                                 required=True)
    affiliation = forms.CharField(widget=forms.TextInput(attrs={'class': 'form-control',
                                                                'placeholder': 'Affiliation'}), max_length=512,
                                  required=True)
    publisher = forms.CharField(widget=forms.TextInput(attrs={'class': 'form-control',
                                                              'placeholder': 'Publisher'}), max_length=45,
                                required=True)
    authors = forms.CharField(widget=forms.Textarea(attrs={'class': 'form-control',
                                                           'placeholder': 'Authors'}), required=True)
    thesaurus_terms_keywords = forms.CharField(widget=forms.Textarea(attrs={'class': 'form-control',
                                                                            'placeholder': 'Thesaurus terms'}),
                                               required=True)
    controlled_terms_keywords = forms.CharField(widget=forms.Textarea(attrs={'class': 'form-control',
                                                                             'placeholder': 'Controlled terms'}),
                                                required=True)
    uncontrolled_terms_keywords = forms.CharField(widget=forms.Textarea(attrs={'class': 'form-control',
                                                                               'placeholder': 'Uncontrolled terms'}),
                                                  required=True)


class SearchForm(forms.Form):
    operator = forms.ChoiceField(choices=[(x.lower(), x) for x in ['AND', 'OR', 'NOT']], widget=forms.Select(attrs={
        'class':'form-control',
    }))
    search_field = forms.CharField(min_length=3, max_length=1024, widget=forms.TextInput(attrs={
        'class':'form-control',
        'required':''
    }))
    criteria = forms.ChoiceField(choices=[(x.lower(), x) for x in ['Title', 'Author', 'Keyword', 'Issue name',
                                                                   'Affiliation', 'Publication year']], widget=forms.Select(attrs={
        'class':'form-control',
    }))
    match = forms.ChoiceField(choices=[(x.lower(), x) for x in ['EXACT', 'LIKE']], widget=forms.Select(attrs={
        'class':'form-control',
    }))