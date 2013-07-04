/*******************************************************************************
 * Copyright (c) 2013 VITRO FP7 Consortium.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     Antoniou Thanasis
 *     Paolo Medagliani
 *     D. Davide Lamanna
 *     Panos Trakadas
 *     Andrea Kropp
 *     Kiriakos Georgouleas
 *     Panagiotis Karkazis
 *     David Ferrer Figueroa
 *     Francesco Ficarola
 *     Stefano Puglia
 ******************************************************************************/
/**
 * missionData fileUpload package originally from:
 *   Class by Pierre-Alexandre Losson -- http://www.telio.be/blog
 *   email : plosson@users.sourceforge.net
 */
package presentation.webgui.vitroappservlet.uploadService.fileupload;

import java.io.OutputStream;
import java.io.IOException;

public class MonitoredOutputStream extends OutputStream
{
  private OutputStream target;
  private OutputStreamListener listener;

  public MonitoredOutputStream(OutputStream target, OutputStreamListener listener)
  {
    this.target = target;
    this.listener = listener;
    this.listener.start();
  }

  public void write(byte b[], int off, int len) throws IOException
  {
    target.write(b, off, len);
    listener.bytesRead(len - off);
  }

  public void write(byte b[]) throws IOException
  {
    target.write(b);
    listener.bytesRead(b.length);
  }

  public void write(int b) throws IOException
  {
    target.write(b);
    listener.bytesRead(1);
  }

  public void close() throws IOException
  {
    target.close();
    listener.done();
  }

  public void flush() throws IOException
  {
    target.flush();
  }
}
