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

/*
** Implementation of https://131002.net/siphash.
*/

package org.purple.smoke;

public class SipHash
{
    private final static long c0 = 0x736f6d6570736575L;
    private final static long c1 = 0x646f72616e646f6dL;
    private final static long c2 = 0x6c7967656e657261L;
    private final static long c3 = 0x7465646279746573L;
    private long m_v0 = 0;
    private long m_v1 = 0;
    private long m_v2 = 0;
    private long m_v3 = 0;

    private long byteArrayToLong(byte bytes[])
    {
	if(bytes == null || bytes.length != 8)
	    return 0;

	long value = 0;

	for(int i = 0; i < bytes.length; i++)
	    value |= (((long) bytes[i]) & 0xff) << (8 * i);

	return value;
    }

    public void SipHash(byte key[])
    {
	if(key == null)
	    return;
	else if(key.length != 16) // 128-bit key.
	    return;
    }
}