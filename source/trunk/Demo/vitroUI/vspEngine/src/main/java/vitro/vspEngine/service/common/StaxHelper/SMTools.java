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
/*
 * SMTools.java
 */

package vitro.vspEngine.service.common.StaxHelper;

import org.codehaus.staxmate.in.SMElementInfo;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputElement;

import java.util.Vector;

/**
 *
 * @author antoniou
 */
public class SMTools {
    
    /** Creates a new instance of SMTools */
    private SMTools() {
    }
    
    
    /**
     * The output of this function is the resultValues (which is also a method argument). It was the only way to update it right with the recursive calls of the function.
     * 
     */
    public static void mygetMultipleElementValuesStax(SMInputCursor parentInpCurs, Vector<String[]> elementsToFollow, Vector<Vector<String>> resultValues)
    throws javax.xml.stream.XMLStreamException 
    {
        // add indexes that index the string array with elements to follow for room names and coordinates                          
        Vector<Integer> idxForElementsToFollow = new Vector<Integer>();
        idxForElementsToFollow.add(Integer.valueOf(0));
        idxForElementsToFollow.add(Integer.valueOf(0));
        // add a result vector. Each position in the result vector corresponds to the corresponding entry in the elementsToFollow Vector.
        for(int i=0; i < idxForElementsToFollow.size(); i++) {
            resultValues.addElement(new Vector<String>());
        }        
        recursElementValueStaxMultiple(parentInpCurs, elementsToFollow, idxForElementsToFollow, resultValues);
    }
    
    /**
     *
     * The output of this function is the tmpVec (which is also a method argument). It was the only way to update it right with the recursive calls of the function.
     *
     */
    private static void recursElementValueStaxMultiple(SMInputCursor parentInpCurs, Vector<String[]> elementsToFollow, Vector<Integer> idxForElementsToFollow, Vector<Vector<String>> tmpVec)
    throws javax.xml.stream.XMLStreamException {
        SMInputCursor childInElement=null;
        childInElement = parentInpCurs.childMixedCursor();
        while (childInElement.getNext() != null) {
            if(childInElement.getCurrEvent().hasText() ) {
                SMElementInfo tmpParentElement = childInElement.getParentTrackedElement();
                String tmpParentName = tmpParentElement.getLocalName();
                for(int i=0; i < idxForElementsToFollow.size(); i++) {
                    int tmpIdx = idxForElementsToFollow.elementAt(i).intValue();
                    String[] tmpStringArray = elementsToFollow.elementAt(i);
                    if(tmpIdx ==  tmpStringArray.length  &&
                            tmpParentName.toLowerCase().equals(tmpStringArray[tmpIdx-1] )) {
                        String myText = childInElement.getText();
                        Vector<String> tmpFlatVec = tmpVec.get(i);
                        tmpFlatVec.add(myText);
                        tmpVec.set(i, tmpFlatVec);
                        break;
                    }
                }
            } else {
                String tmpCurrElementName = childInElement.getLocalName();
                for(int i=0; i < idxForElementsToFollow.size(); i++) {
                    int tmpIndx = idxForElementsToFollow.elementAt(i).intValue();
                    String[] tmpStringArray = elementsToFollow.elementAt(i);
                    if(tmpIndx < tmpStringArray.length &&
                            tmpCurrElementName.toLowerCase().equals(tmpStringArray[tmpIndx].toLowerCase() ) ) {
                        // we use a copy constructor to not proliferate the index values for idxForElementsToFollow in the recursion
                        Vector<Integer> tmpIdxForElementsVector = new Vector<Integer>(idxForElementsToFollow);
                        tmpIdxForElementsVector.set(i, Integer.valueOf(tmpIndx + 1));
                        // also advance by one all the other indices that show this element in their corresponding slot in the String elementsToFollow array
                        for(int j = i+1; j < idxForElementsToFollow.size(); j++) {
                            int tmpIndx2 = idxForElementsToFollow.elementAt(j).intValue();
                            String[] tmpStringArray2 = elementsToFollow.elementAt(j);
                            if(tmpIndx2 < tmpStringArray2.length && tmpCurrElementName.toLowerCase().equals(tmpStringArray2[tmpIndx2].toLowerCase()) ) {
                                tmpIdxForElementsVector.set(j, Integer.valueOf(tmpIndx2 + 1));
                            }
                        }
                        recursElementValueStaxMultiple(childInElement, elementsToFollow, tmpIdxForElementsVector, tmpVec );
                        break;
                    }
                }
            }
        }
    }
    
    
    /**
     *
     * Starts from a parent InputCursor (getting his children) and follows the elementsToFollow Array, in the given sequence, until
     * the final element is found. The value(s) of this element (or all elements found in such a sequence under the parent cursor)
     * are stored inside the returned vector of string.
     * The method is called recursively.
     */
    public static Vector<String> mygetElementValueStax(SMInputCursor parentInpCurs, String[] elementsToFollow, int indexforFollowArray)
    throws javax.xml.stream.XMLStreamException {
        Vector<String> tmpVec = new Vector<String>();
        SMInputCursor childInElement = parentInpCurs.childMixedCursor();
        String myText="";
        while (childInElement.getNext() != null) {
            if(childInElement.getCurrEvent().hasText() &&
                    indexforFollowArray == elementsToFollow.length) {
                myText = childInElement.getText();
                tmpVec.add(myText);
            } else if(!childInElement.getCurrEvent().hasText() &&
                    indexforFollowArray < elementsToFollow.length &&
                    childInElement.getLocalName().toLowerCase().equals(elementsToFollow[indexforFollowArray].toLowerCase() ) ) {
                tmpVec.addAll(mygetElementValueStax(childInElement, elementsToFollow, indexforFollowArray+1));
            }
        }
        return tmpVec;
    }
    
      
    public static void cloneAllAttributestoOutputElement(SMOutputElement outputElement, SMInputCursor inputElement)
    throws javax.xml.stream.XMLStreamException
    {
          for(int j = 0; j < inputElement.getAttrCount(); j++)
          {
              outputElement.addAttribute(inputElement.getAttrLocalName(j), inputElement.getAttrValue(j)); 
          }
    }  
    
}
