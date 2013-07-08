/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis (Research Academic Computer Technology Institute)
 * #     Paolo Medagliani (Thales Communications & Security)
 * #     D. Davide Lamanna (WLAB SRL)
 * #     Alessandro Leoni (WLAB SRL)
 * #     Francesco Ficarola (WLAB SRL)
 * #     Stefano Puglia (WLAB SRL)
 * #     Panos Trakadas (Technological Educational Institute of Chalkida)
 * #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
 * #     Andrea Kropp (Selex ES)
 * #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
 * #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
 * #
 * #--------------------------------------------------------------------------
 */

package vitro.virtualsensor.utils;


import java.io.File;
import java.io.IOException;

/**
 *
 * @author Andres
 */
public class TextFile extends File {
    
    private String
            content;
    
    private boolean readable, writable;
    
    private FileWorker
            worker;
    
    public TextFile (String path) {
        super(path);
        worker = new FileWorker();
        readable = this.exists() && this.canRead();
        writable = this.exists() && this.canWrite();
    }
    
    public TextFile (File f) throws IOException {
        this(f.getCanonicalPath());
    }
    
    public String getText () {
        if (readable && content == null) {
            content = worker.readFile(this);
        }
        return content;
    }
    
    public boolean writeText (String text) {
        if (text != null && !text.isEmpty()) {
            return worker.writeFile(this, text);
        } else return false;
    }
    
}
