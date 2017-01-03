from django.conf.urls import url, include
from django.contrib import admin
from oauth2_provider.views import base

from InertiaDjango.views import MessageList

urlpatterns = [
	#url(r'^o/', include('oauth2_provider.urls', namespace='oauth2_provider')),
    #url(r'^admin/', admin.site.urls),
    url(r'o/token/$', base.TokenView.as_view(), name = 'token'),
    url(r'^message/$', MessageList.as_view()),
]
