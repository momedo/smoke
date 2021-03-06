/*
** Copyright (c) Alexis Megas.
** All rights reserved.
**
** Redistribution and use in source and binary forms, with or without
** modification, are permitted provided that the following conditions
** are met:
** 1. Redistributions of source code must retain the above copyright
**    notice, this list of conditions and the following disclaimer.
** 2. Redistributions in binary form must reproduce the above copyright
**    notice, this list of conditions and the following disclaimer in the
**    documentation and/or other materials provided with the distribution.
** 3. The name of the author may not be used to endorse or promote products
**    derived from Smoke without specific prior written permission.
**
** SMOKE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
** IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
** OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
** IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
** INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
** NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
** DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
** THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
** (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
** SMOKE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.purple.smoke;

import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class State
{
    private ArrayList<MessageElement> m_chatMessages = null;
    private Bundle m_bundle = null;
    private Map<String, FireChannel> m_fireChannels = null;
    private final ReentrantReadWriteLock m_bundleMutex =
	new ReentrantReadWriteLock();
    private static State s_instance = null;

    private State()
    {
	m_bundle = new Bundle();
	setAuthenticated(false);
    }

    public CharSequence getCharSequence(String key)
    {
	m_bundleMutex.readLock().lock();

	try
	{
	    return m_bundle.getCharSequence(key, "");
	}
	finally
	{
	    m_bundleMutex.readLock().unlock();
	}
    }

    public FireChannel fireChannel(String name)
    {
	if(name == null)
	    return null;

	if(m_fireChannels != null && m_fireChannels.containsKey(name))
	    return m_fireChannels.get(name);

	return null;
    }

    public Map<String, FireChannel> fireChannels()
    {
	return m_fireChannels;
    }

    public String getString(String key)
    {
	m_bundleMutex.readLock().lock();

	try
	{
	    return m_bundle.getString(key, "");
	}
	finally
	{
	    m_bundleMutex.readLock().unlock();
	}
    }

    public String nameOfFireFromView(View view)
    {
	if(m_fireChannels == null || view == null)
	    return "";

	for(Map.Entry<String, FireChannel> entry : m_fireChannels.entrySet())
	    if(entry.getValue() != null)
		if(entry.getValue().view() == view)
		    return entry.getValue().name();

	return "";
    }

    public boolean chatCheckBoxIsSelected(int oid)
    {
	m_bundleMutex.readLock().lock();

	try
	{
	    return m_bundle.getChar
		("chat_checkbox_" + String.valueOf(oid), '0') == '1';
	}
	finally
	{
	    m_bundleMutex.readLock().unlock();
	}
    }

    public boolean containsFire(String name)
    {
	return m_fireChannels != null && m_fireChannels.containsKey(name);
    }

    public boolean isAuthenticated()
    {
	m_bundleMutex.readLock().lock();

	try
	{
	    return m_bundle.getChar("is_authenticated", '0') == '1';
	}
	finally
	{
	    m_bundleMutex.readLock().unlock();
	}
    }

    public boolean neighborsEcho()
    {
	m_bundleMutex.readLock().lock();

	try
	{
	    return m_bundle.getChar("neighbors_echo", '0') == '1';
	}
	finally
	{
	    m_bundleMutex.readLock().unlock();
	}
    }

    public char getChar(String key)
    {
	m_bundleMutex.readLock().lock();

	try
	{
	    return m_bundle.getChar(key, '0');
	}
	finally
	{
	    m_bundleMutex.readLock().unlock();
	}
    }

    public int chatCheckedParticipants()
    {
	m_bundleMutex.readLock().lock();

	try
	{
	    return m_bundle.getInt("chat_checkbox_counter", 0);
	}
	finally
	{
	    m_bundleMutex.readLock().unlock();
	}
    }

    public long chatSequence(String sipHashId)
    {
	m_bundleMutex.readLock().lock();

	try
	{
	    return m_bundle.getLong("chat_sequence" + sipHashId, 1);
	}
	finally
	{
	    m_bundleMutex.readLock().unlock();
	}
    }

    public static synchronized State getInstance()
    {
	if(s_instance == null)
	    s_instance = new State();

	return s_instance;
    }

    public synchronized ArrayList<MessageElement> chatLog()
    {
	return m_chatMessages;
    }

    public synchronized void clearChatLog()
    {
	if(m_chatMessages != null)
	    m_chatMessages.clear();

	m_chatMessages = null;
    }

    public synchronized void logChatMessage(String message,
					    String name,
					    String sipHashId,
					    long sequence,
					    long timestamp)
    {
	if(message == null || name == null || sipHashId == null)
	    return;

	if(m_chatMessages == null)
	    m_chatMessages = new ArrayList<> ();

	MessageElement messageElement = new MessageElement();

	messageElement.m_id = sipHashId;
	messageElement.m_message = message;
	messageElement.m_name = name;
	messageElement.m_sequence = sequence;
	messageElement.m_timestamp = timestamp;
	m_chatMessages.add(messageElement);
    }

    public void addFire(FireChannel fireChannel)
    {
	if(fireChannel == null)
	    return;

	if(m_fireChannels == null)
	    m_fireChannels = new TreeMap<> ();
	else if(m_fireChannels.containsKey(fireChannel.name()))
	    return;

	m_fireChannels.put(fireChannel.name(), fireChannel);
    }

    public void incrementChatSequence(String sipHashId)
    {
	long sequence = chatSequence(sipHashId);

	m_bundleMutex.writeLock().lock();

	try
	{
	    m_bundle.putLong("chat_sequence" + sipHashId, sequence + 1);
	}
	finally
	{
	    m_bundleMutex.writeLock().unlock();
	}
    }

    public void removeChatCheckBoxOid(int oid)
    {
	m_bundleMutex.writeLock().lock();

	try
	{
	    m_bundle.remove("chat_checkbox_" + String.valueOf(oid));
	}
	finally
	{
	    m_bundleMutex.writeLock().unlock();
	}
    }

    public void removeFireChannel(String name)
    {
	if(m_fireChannels == null)
	    return;

	m_fireChannels.remove(name);
    }

    public void removeKey(String key)
    {
	m_bundleMutex.writeLock().lock();

	try
	{
	    m_bundle.remove(key);
	}
	finally
	{
	    m_bundleMutex.writeLock().unlock();
	}
    }

    public void reset()
    {
	clearChatLog();
	m_bundleMutex.writeLock().lock();

	try
	{
	    m_bundle.clear();
	}
	finally
	{
	    m_bundleMutex.writeLock().unlock();
	}

	if(m_fireChannels != null)
	    m_fireChannels.clear();
    }

    public void setAuthenticated(boolean state)
    {
	m_bundleMutex.writeLock().lock();

	try
	{
	    m_bundle.putChar("is_authenticated", state ? '1' : '0');
	}
	finally
	{
	    m_bundleMutex.writeLock().unlock();
	}
    }

    public void setChatCheckBoxSelected(int oid, boolean checked)
    {
	boolean contains = false;

	m_bundleMutex.readLock().lock();

	try
	{
	    contains = m_bundle.containsKey
		("chat_checkbox_" + String.valueOf(oid));
	}
	finally
	{
	    m_bundleMutex.readLock().unlock();
	}

	if(checked)
	{
	    m_bundleMutex.writeLock().lock();

	    try
	    {
		m_bundle.putChar("chat_checkbox_" + String.valueOf(oid), '1');

		if(!contains)
		    m_bundle.putInt
			("chat_checkbox_counter",
			 chatCheckedParticipants() + 1);
	    }
	    finally
	    {
		m_bundleMutex.writeLock().unlock();
	    }
	}
	else
	{
	    m_bundleMutex.writeLock().lock();

	    try
	    {
		m_bundle.remove("chat_checkbox_" + String.valueOf(oid));
	    }
	    finally
	    {
		m_bundleMutex.writeLock().unlock();
	    }

	    if(contains)
	    {
		int counter = chatCheckedParticipants(); // Read lock.

		if(counter > 0)
		    counter -= 1;

		m_bundleMutex.writeLock().lock();

		try
		{
		    m_bundle.putInt("chat_checkbox_counter", counter);
		}
		finally
		{
		    m_bundleMutex.writeLock().unlock();
		}
	    }
	}
    }

    public void setNeighborsEcho(boolean state)
    {
	m_bundleMutex.writeLock().lock();

	try
	{
	    m_bundle.putChar("neighbors_echo", state ? '1' : '0');
	}
	finally
	{
	    m_bundleMutex.writeLock().unlock();
	}
    }

    public void setString(String key, String value)
    {
	m_bundleMutex.writeLock().lock();

	try
	{
	    m_bundle.putString(key, value);
	}
	finally
	{
	    m_bundleMutex.writeLock().unlock();
	}
    }

    public void writeChar(String key, char character)
    {
	m_bundleMutex.writeLock().lock();

	try
	{
	    m_bundle.putChar(key, character);
	}
	finally
	{
	    m_bundleMutex.writeLock().unlock();
	}
    }

    public void writeCharSequence(String key, CharSequence text)
    {
	m_bundleMutex.writeLock().lock();

	try
	{
	    m_bundle.putCharSequence(key, text);
	}
	finally
	{
	    m_bundleMutex.writeLock().unlock();
	}
    }
}
