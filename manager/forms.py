from django import forms


class LoginForm(forms.Form):
    login = forms.CharField(widget=forms.TextInput(
        attrs={
            'class': 'form-control',
            'required': '',
            'placeholder': 'Login',
            'id': 'login'
        }
    ))
    password = forms.CharField(widget=forms.PasswordInput(
        attrs={
            'class': 'form-control',
            'required': '',
            'placeholder': 'Password',
            'id': 'password'
        }
    ))
