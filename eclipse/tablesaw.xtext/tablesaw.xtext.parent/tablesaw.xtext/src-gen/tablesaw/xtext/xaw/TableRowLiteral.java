/**
 * generated by Xtext 2.14.0
 */
package tablesaw.xtext.xaw;

import org.eclipse.emf.common.util.EList;

import org.eclipse.xtext.xbase.XExpression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Table Row Literal</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tablesaw.xtext.xaw.TableRowLiteral#getExpressions <em>Expressions</em>}</li>
 * </ul>
 *
 * @see tablesaw.xtext.xaw.XawPackage#getTableRowLiteral()
 * @model
 * @generated
 */
public interface TableRowLiteral extends XExpression
{
  /**
   * Returns the value of the '<em><b>Expressions</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.xtext.xbase.XExpression}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Expressions</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Expressions</em>' containment reference list.
   * @see tablesaw.xtext.xaw.XawPackage#getTableRowLiteral_Expressions()
   * @model containment="true"
   * @generated
   */
  EList<XExpression> getExpressions();

} // TableRowLiteral
