/**
 * generated by Xtext 2.14.0
 */
package tablesaw.xtext.xaw;

import org.eclipse.emf.common.util.EList;

import org.eclipse.xtext.xbase.XBlockExpression;

import org.eclipse.xtext.xtype.XImportSection;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xaw</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tablesaw.xtext.xaw.Xaw#getImportSection <em>Import Section</em>}</li>
 *   <li>{@link tablesaw.xtext.xaw.Xaw#getQName <em>QName</em>}</li>
 *   <li>{@link tablesaw.xtext.xaw.Xaw#getMethods <em>Methods</em>}</li>
 * </ul>
 *
 * @see tablesaw.xtext.xaw.XawPackage#getXaw()
 * @model
 * @generated
 */
public interface Xaw extends XBlockExpression
{
  /**
   * Returns the value of the '<em><b>Import Section</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Import Section</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Import Section</em>' containment reference.
   * @see #setImportSection(XImportSection)
   * @see tablesaw.xtext.xaw.XawPackage#getXaw_ImportSection()
   * @model containment="true"
   * @generated
   */
  XImportSection getImportSection();

  /**
   * Sets the value of the '{@link tablesaw.xtext.xaw.Xaw#getImportSection <em>Import Section</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Import Section</em>' containment reference.
   * @see #getImportSection()
   * @generated
   */
  void setImportSection(XImportSection value);

  /**
   * Returns the value of the '<em><b>QName</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>QName</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>QName</em>' attribute.
   * @see #setQName(String)
   * @see tablesaw.xtext.xaw.XawPackage#getXaw_QName()
   * @model
   * @generated
   */
  String getQName();

  /**
   * Sets the value of the '{@link tablesaw.xtext.xaw.Xaw#getQName <em>QName</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>QName</em>' attribute.
   * @see #getQName()
   * @generated
   */
  void setQName(String value);

  /**
   * Returns the value of the '<em><b>Methods</b></em>' containment reference list.
   * The list contents are of type {@link tablesaw.xtext.xaw.XMethod}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Methods</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Methods</em>' containment reference list.
   * @see tablesaw.xtext.xaw.XawPackage#getXaw_Methods()
   * @model containment="true"
   * @generated
   */
  EList<XMethod> getMethods();

} // Xaw
