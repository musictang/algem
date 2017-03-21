/*
 *  Copyright 2010 Georgios Migdos.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package net.algem.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 2.13.0 16/03/2017
 * @see https://gmigdos.wordpress.com/2010/04/08/java-how-to-auto-detect-a-files-encoding/
 */
public class SimpleCharsetDecoder
{

  public Charset detectCharset(File f, String[] charsets) throws IOException {
    Charset charset = null;

    for (String charsetName : charsets) {
      charset = detectCharset(f, Charset.forName(charsetName));
      if (charset != null) {
        break;
      }
    }

    return charset;
  }

  private Charset detectCharset(File f, final Charset charset) throws IOException {
    boolean identified = false;
    try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(f))) {
      CharsetDecoder d = charset.newDecoder();
      d.reset();
      byte[] buffer = new byte[512];
      while ((input.read(buffer) != -1) && (!identified)) {
        identified = identify(buffer, d);
      }
    }

    if (identified) {
      return charset;
    } else {
      return null;
    }

  }

  private boolean identify(byte[] bytes, CharsetDecoder decoder) {
    try {
      decoder.decode(ByteBuffer.wrap(bytes));
    } catch (CharacterCodingException e) {
      return false;
    }
    return true;
  }

}
