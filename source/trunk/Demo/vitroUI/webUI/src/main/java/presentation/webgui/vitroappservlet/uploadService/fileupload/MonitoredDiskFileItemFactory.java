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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.File;

public class MonitoredDiskFileItemFactory extends DiskFileItemFactory
{
  private OutputStreamListener listener = null;

  public MonitoredDiskFileItemFactory(OutputStreamListener listener)
  {
    super();
    this.listener = listener;
  }

  public MonitoredDiskFileItemFactory(int sizeThreshold, File repository, OutputStreamListener listener)
  {
    super(sizeThreshold, repository);
    this.listener = listener;
  }

  public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName)
  {
    return new MonitoredDiskFileItem(fieldName, contentType, isFormField, fileName, getSizeThreshold(), getRepository(), listener);
  }
}
