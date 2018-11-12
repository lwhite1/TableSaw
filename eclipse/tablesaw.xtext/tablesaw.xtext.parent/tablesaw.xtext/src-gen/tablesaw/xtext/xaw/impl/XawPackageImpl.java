/**
 * generated by Xtext 2.14.0
 */
package tablesaw.xtext.xaw.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.xtext.common.types.TypesPackage;

import org.eclipse.xtext.xbase.XbasePackage;

import org.eclipse.xtext.xtype.XtypePackage;

import tablesaw.xtext.xaw.InlineTableRow;
import tablesaw.xtext.xaw.TableColumn;
import tablesaw.xtext.xaw.TableLiteral;
import tablesaw.xtext.xaw.TableRowLiteral;
import tablesaw.xtext.xaw.XMethod;
import tablesaw.xtext.xaw.Xaw;
import tablesaw.xtext.xaw.XawFactory;
import tablesaw.xtext.xaw.XawPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class XawPackageImpl extends EPackageImpl implements XawPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xMethodEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xawEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass tableLiteralEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass tableColumnEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass inlineTableRowEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass tableRowLiteralEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see tablesaw.xtext.xaw.XawPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private XawPackageImpl()
  {
    super(eNS_URI, XawFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link XawPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static XawPackage init()
  {
    if (isInited) return (XawPackage)EPackage.Registry.INSTANCE.getEPackage(XawPackage.eNS_URI);

    // Obtain or create and register package
    Object registeredXawPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    XawPackageImpl theXawPackage = registeredXawPackage instanceof XawPackageImpl ? (XawPackageImpl)registeredXawPackage : new XawPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    TypesPackage.eINSTANCE.eClass();
    XbasePackage.eINSTANCE.eClass();
    XtypePackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theXawPackage.createPackageContents();

    // Initialize created meta-data
    theXawPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theXawPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(XawPackage.eNS_URI, theXawPackage);
    return theXawPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXMethod()
  {
    return xMethodEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXMethod_ReturnType()
  {
    return (EReference)xMethodEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXMethod_Name()
  {
    return (EAttribute)xMethodEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXMethod_Parameters()
  {
    return (EReference)xMethodEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXMethod_Body()
  {
    return (EReference)xMethodEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXaw()
  {
    return xawEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXaw_ImportSection()
  {
    return (EReference)xawEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXaw_QName()
  {
    return (EAttribute)xawEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXaw_Methods()
  {
    return (EReference)xawEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTableLiteral()
  {
    return tableLiteralEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTableLiteral_Name()
  {
    return (EAttribute)tableLiteralEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTableColumn()
  {
    return tableColumnEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTableColumn_Type()
  {
    return (EReference)tableColumnEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTableColumn_Name()
  {
    return (EAttribute)tableColumnEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTableColumn_Expression()
  {
    return (EReference)tableColumnEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getInlineTableRow()
  {
    return inlineTableRowEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTableRowLiteral()
  {
    return tableRowLiteralEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTableRowLiteral_Expressions()
  {
    return (EReference)tableRowLiteralEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XawFactory getXawFactory()
  {
    return (XawFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    xMethodEClass = createEClass(XMETHOD);
    createEReference(xMethodEClass, XMETHOD__RETURN_TYPE);
    createEAttribute(xMethodEClass, XMETHOD__NAME);
    createEReference(xMethodEClass, XMETHOD__PARAMETERS);
    createEReference(xMethodEClass, XMETHOD__BODY);

    xawEClass = createEClass(XAW);
    createEReference(xawEClass, XAW__IMPORT_SECTION);
    createEAttribute(xawEClass, XAW__QNAME);
    createEReference(xawEClass, XAW__METHODS);

    tableLiteralEClass = createEClass(TABLE_LITERAL);
    createEAttribute(tableLiteralEClass, TABLE_LITERAL__NAME);

    tableColumnEClass = createEClass(TABLE_COLUMN);
    createEReference(tableColumnEClass, TABLE_COLUMN__TYPE);
    createEAttribute(tableColumnEClass, TABLE_COLUMN__NAME);
    createEReference(tableColumnEClass, TABLE_COLUMN__EXPRESSION);

    inlineTableRowEClass = createEClass(INLINE_TABLE_ROW);

    tableRowLiteralEClass = createEClass(TABLE_ROW_LITERAL);
    createEReference(tableRowLiteralEClass, TABLE_ROW_LITERAL__EXPRESSIONS);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    TypesPackage theTypesPackage = (TypesPackage)EPackage.Registry.INSTANCE.getEPackage(TypesPackage.eNS_URI);
    XbasePackage theXbasePackage = (XbasePackage)EPackage.Registry.INSTANCE.getEPackage(XbasePackage.eNS_URI);
    XtypePackage theXtypePackage = (XtypePackage)EPackage.Registry.INSTANCE.getEPackage(XtypePackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    xawEClass.getESuperTypes().add(theXbasePackage.getXBlockExpression());
    tableLiteralEClass.getESuperTypes().add(theXbasePackage.getXBlockExpression());
    tableColumnEClass.getESuperTypes().add(theXbasePackage.getXExpression());
    inlineTableRowEClass.getESuperTypes().add(theXbasePackage.getXBlockExpression());
    tableRowLiteralEClass.getESuperTypes().add(theXbasePackage.getXExpression());

    // Initialize classes and features; add operations and parameters
    initEClass(xMethodEClass, XMethod.class, "XMethod", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getXMethod_ReturnType(), theTypesPackage.getJvmTypeReference(), null, "returnType", null, 0, 1, XMethod.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXMethod_Name(), ecorePackage.getEString(), "name", null, 0, 1, XMethod.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXMethod_Parameters(), theTypesPackage.getJvmFormalParameter(), null, "parameters", null, 0, -1, XMethod.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXMethod_Body(), theXbasePackage.getXExpression(), null, "body", null, 0, 1, XMethod.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xawEClass, Xaw.class, "Xaw", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getXaw_ImportSection(), theXtypePackage.getXImportSection(), null, "importSection", null, 0, 1, Xaw.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXaw_QName(), ecorePackage.getEString(), "qName", null, 0, 1, Xaw.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXaw_Methods(), this.getXMethod(), null, "methods", null, 0, -1, Xaw.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(tableLiteralEClass, TableLiteral.class, "TableLiteral", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTableLiteral_Name(), ecorePackage.getEString(), "name", null, 0, 1, TableLiteral.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(tableColumnEClass, TableColumn.class, "TableColumn", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTableColumn_Type(), theTypesPackage.getJvmTypeReference(), null, "type", null, 0, 1, TableColumn.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTableColumn_Name(), ecorePackage.getEString(), "name", null, 0, 1, TableColumn.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTableColumn_Expression(), theXbasePackage.getXExpression(), null, "expression", null, 0, 1, TableColumn.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(inlineTableRowEClass, InlineTableRow.class, "InlineTableRow", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(tableRowLiteralEClass, TableRowLiteral.class, "TableRowLiteral", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTableRowLiteral_Expressions(), theXbasePackage.getXExpression(), null, "expressions", null, 0, -1, TableRowLiteral.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} //XawPackageImpl
