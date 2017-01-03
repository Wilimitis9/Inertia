# -*- coding: utf-8 -*-
# Generated by Django 1.10.4 on 2017-01-03 02:26
from __future__ import unicode_literals

from django.db import migrations, models
import uuid


class Migration(migrations.Migration):

    dependencies = [
        ('InertiaDjango', '0003_message_user'),
    ]

    operations = [
        migrations.AddField(
            model_name='message',
            name='uuid',
            field=models.UUIDField(default=uuid.uuid4),
        ),
    ]
