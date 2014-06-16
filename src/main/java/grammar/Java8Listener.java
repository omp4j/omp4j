// Generated from Java8.g4 by ANTLR 4.2
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Java8Parser}.
 */
public interface Java8Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Java8Parser#innerCreator}.
	 * @param ctx the parse tree
	 */
	void enterInnerCreator(@NotNull Java8Parser.InnerCreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#innerCreator}.
	 * @param ctx the parse tree
	 */
	void exitInnerCreator(@NotNull Java8Parser.InnerCreatorContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#genericMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGenericMethodDeclaration(@NotNull Java8Parser.GenericMethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#genericMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGenericMethodDeclaration(@NotNull Java8Parser.GenericMethodDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(@NotNull Java8Parser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(@NotNull Java8Parser.ExpressionListContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration(@NotNull Java8Parser.TypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration(@NotNull Java8Parser.TypeDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void enterForUpdate(@NotNull Java8Parser.ForUpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void exitForUpdate(@NotNull Java8Parser.ForUpdateContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotation}.
	 * @param ctx the parse tree
	 */
	void enterAnnotation(@NotNull Java8Parser.AnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotation}.
	 * @param ctx the parse tree
	 */
	void exitAnnotation(@NotNull Java8Parser.AnnotationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumConstant}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstant(@NotNull Java8Parser.EnumConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumConstant}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstant(@NotNull Java8Parser.EnumConstantContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration(@NotNull Java8Parser.ImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration(@NotNull Java8Parser.ImportDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationMethodOrConstantRest}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationMethodOrConstantRest(@NotNull Java8Parser.AnnotationMethodOrConstantRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationMethodOrConstantRest}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationMethodOrConstantRest(@NotNull Java8Parser.AnnotationMethodOrConstantRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumConstantName}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstantName(@NotNull Java8Parser.EnumConstantNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumConstantName}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstantName(@NotNull Java8Parser.EnumConstantNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#finallyBlock}.
	 * @param ctx the parse tree
	 */
	void enterFinallyBlock(@NotNull Java8Parser.FinallyBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#finallyBlock}.
	 * @param ctx the parse tree
	 */
	void exitFinallyBlock(@NotNull Java8Parser.FinallyBlockContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarators(@NotNull Java8Parser.VariableDeclaratorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarators(@NotNull Java8Parser.VariableDeclaratorsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#elementValuePairs}.
	 * @param ctx the parse tree
	 */
	void enterElementValuePairs(@NotNull Java8Parser.ElementValuePairsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#elementValuePairs}.
	 * @param ctx the parse tree
	 */
	void exitElementValuePairs(@NotNull Java8Parser.ElementValuePairsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMethodDeclaration(@NotNull Java8Parser.InterfaceMethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMethodDeclaration(@NotNull Java8Parser.InterfaceMethodDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceBodyDeclaration(@NotNull Java8Parser.InterfaceBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceBodyDeclaration(@NotNull Java8Parser.InterfaceBodyDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumConstants}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstants(@NotNull Java8Parser.EnumConstantsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumConstants}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstants(@NotNull Java8Parser.EnumConstantsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#catchClause}.
	 * @param ctx the parse tree
	 */
	void enterCatchClause(@NotNull Java8Parser.CatchClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#catchClause}.
	 * @param ctx the parse tree
	 */
	void exitCatchClause(@NotNull Java8Parser.CatchClauseContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void enterConstantExpression(@NotNull Java8Parser.ConstantExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void exitConstantExpression(@NotNull Java8Parser.ConstantExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterEnumDeclaration(@NotNull Java8Parser.EnumDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitEnumDeclaration(@NotNull Java8Parser.EnumDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#explicitGenericInvocationSuffix}.
	 * @param ctx the parse tree
	 */
	void enterExplicitGenericInvocationSuffix(@NotNull Java8Parser.ExplicitGenericInvocationSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#explicitGenericInvocationSuffix}.
	 * @param ctx the parse tree
	 */
	void exitExplicitGenericInvocationSuffix(@NotNull Java8Parser.ExplicitGenericInvocationSuffixContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameter(@NotNull Java8Parser.TypeParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameter(@NotNull Java8Parser.TypeParameterContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumBodyDeclarations}.
	 * @param ctx the parse tree
	 */
	void enterEnumBodyDeclarations(@NotNull Java8Parser.EnumBodyDeclarationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumBodyDeclarations}.
	 * @param ctx the parse tree
	 */
	void exitEnumBodyDeclarations(@NotNull Java8Parser.EnumBodyDeclarationsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeBound}.
	 * @param ctx the parse tree
	 */
	void enterTypeBound(@NotNull Java8Parser.TypeBoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeBound}.
	 * @param ctx the parse tree
	 */
	void exitTypeBound(@NotNull Java8Parser.TypeBoundContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void enterStatementExpression(@NotNull Java8Parser.StatementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void exitStatementExpression(@NotNull Java8Parser.StatementExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitializer(@NotNull Java8Parser.VariableInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitializer(@NotNull Java8Parser.VariableInitializerContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(@NotNull Java8Parser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(@NotNull Java8Parser.BlockContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#genericInterfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGenericInterfaceMethodDeclaration(@NotNull Java8Parser.GenericInterfaceMethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#genericInterfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGenericInterfaceMethodDeclaration(@NotNull Java8Parser.GenericInterfaceMethodDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclarationStatement(@NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclarationStatement(@NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#superSuffix}.
	 * @param ctx the parse tree
	 */
	void enterSuperSuffix(@NotNull Java8Parser.SuperSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#superSuffix}.
	 * @param ctx the parse tree
	 */
	void exitSuperSuffix(@NotNull Java8Parser.SuperSuffixContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclaration(@NotNull Java8Parser.FieldDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclaration(@NotNull Java8Parser.FieldDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterList(@NotNull Java8Parser.FormalParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterList(@NotNull Java8Parser.FormalParameterListContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#explicitGenericInvocation}.
	 * @param ctx the parse tree
	 */
	void enterExplicitGenericInvocation(@NotNull Java8Parser.ExplicitGenericInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#explicitGenericInvocation}.
	 * @param ctx the parse tree
	 */
	void exitExplicitGenericInvocation(@NotNull Java8Parser.ExplicitGenericInvocationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#parExpression}.
	 * @param ctx the parse tree
	 */
	void enterParExpression(@NotNull Java8Parser.ParExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#parExpression}.
	 * @param ctx the parse tree
	 */
	void exitParExpression(@NotNull Java8Parser.ParExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabel(@NotNull Java8Parser.SwitchLabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabel(@NotNull Java8Parser.SwitchLabelContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameters(@NotNull Java8Parser.TypeParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameters(@NotNull Java8Parser.TypeParametersContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedName(@NotNull Java8Parser.QualifiedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedName(@NotNull Java8Parser.QualifiedNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(@NotNull Java8Parser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(@NotNull Java8Parser.ClassDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationConstantRest}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationConstantRest(@NotNull Java8Parser.AnnotationConstantRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationConstantRest}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationConstantRest(@NotNull Java8Parser.AnnotationConstantRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(@NotNull Java8Parser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(@NotNull Java8Parser.ArgumentsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void enterConstructorBody(@NotNull Java8Parser.ConstructorBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void exitConstructorBody(@NotNull Java8Parser.ConstructorBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameters(@NotNull Java8Parser.FormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameters(@NotNull Java8Parser.FormalParametersContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgument(@NotNull Java8Parser.TypeArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgument(@NotNull Java8Parser.TypeArgumentContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInit(@NotNull Java8Parser.ForInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInit(@NotNull Java8Parser.ForInitContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeDeclaration(@NotNull Java8Parser.AnnotationTypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeDeclaration(@NotNull Java8Parser.AnnotationTypeDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(@NotNull Java8Parser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(@NotNull Java8Parser.ExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#resources}.
	 * @param ctx the parse tree
	 */
	void enterResources(@NotNull Java8Parser.ResourcesContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#resources}.
	 * @param ctx the parse tree
	 */
	void exitResources(@NotNull Java8Parser.ResourcesContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter(@NotNull Java8Parser.FormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter(@NotNull Java8Parser.FormalParameterContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(@NotNull Java8Parser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(@NotNull Java8Parser.TypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#elementValueArrayInitializer}.
	 * @param ctx the parse tree
	 */
	void enterElementValueArrayInitializer(@NotNull Java8Parser.ElementValueArrayInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#elementValueArrayInitializer}.
	 * @param ctx the parse tree
	 */
	void exitElementValueArrayInitializer(@NotNull Java8Parser.ElementValueArrayInitializerContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationName}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationName(@NotNull Java8Parser.AnnotationNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationName}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationName(@NotNull Java8Parser.AnnotationNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#enhancedForControl}.
	 * @param ctx the parse tree
	 */
	void enterEnhancedForControl(@NotNull Java8Parser.EnhancedForControlContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enhancedForControl}.
	 * @param ctx the parse tree
	 */
	void exitEnhancedForControl(@NotNull Java8Parser.EnhancedForControlContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationMethodRest}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationMethodRest(@NotNull Java8Parser.AnnotationMethodRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationMethodRest}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationMethodRest(@NotNull Java8Parser.AnnotationMethodRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(@NotNull Java8Parser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(@NotNull Java8Parser.PrimaryContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(@NotNull Java8Parser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(@NotNull Java8Parser.ClassBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#classOrInterfaceModifier}.
	 * @param ctx the parse tree
	 */
	void enterClassOrInterfaceModifier(@NotNull Java8Parser.ClassOrInterfaceModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classOrInterfaceModifier}.
	 * @param ctx the parse tree
	 */
	void exitClassOrInterfaceModifier(@NotNull Java8Parser.ClassOrInterfaceModifierContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void enterDefaultValue(@NotNull Java8Parser.DefaultValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void exitDefaultValue(@NotNull Java8Parser.DefaultValueContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#variableModifier}.
	 * @param ctx the parse tree
	 */
	void enterVariableModifier(@NotNull Java8Parser.VariableModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#variableModifier}.
	 * @param ctx the parse tree
	 */
	void exitVariableModifier(@NotNull Java8Parser.VariableModifierContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#constDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstDeclaration(@NotNull Java8Parser.ConstDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstDeclaration(@NotNull Java8Parser.ConstDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#createdName}.
	 * @param ctx the parse tree
	 */
	void enterCreatedName(@NotNull Java8Parser.CreatedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#createdName}.
	 * @param ctx the parse tree
	 */
	void exitCreatedName(@NotNull Java8Parser.CreatedNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceDeclaration(@NotNull Java8Parser.InterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceDeclaration(@NotNull Java8Parser.InterfaceDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#constantDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterConstantDeclarator(@NotNull Java8Parser.ConstantDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constantDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitConstantDeclarator(@NotNull Java8Parser.ConstantDeclaratorContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#catchType}.
	 * @param ctx the parse tree
	 */
	void enterCatchType(@NotNull Java8Parser.CatchTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#catchType}.
	 * @param ctx the parse tree
	 */
	void exitCatchType(@NotNull Java8Parser.CatchTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void enterTypeArguments(@NotNull Java8Parser.TypeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void exitTypeArguments(@NotNull Java8Parser.TypeArgumentsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#classCreatorRest}.
	 * @param ctx the parse tree
	 */
	void enterClassCreatorRest(@NotNull Java8Parser.ClassCreatorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classCreatorRest}.
	 * @param ctx the parse tree
	 */
	void exitClassCreatorRest(@NotNull Java8Parser.ClassCreatorRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifier(@NotNull Java8Parser.ModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifier(@NotNull Java8Parser.ModifierContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(@NotNull Java8Parser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(@NotNull Java8Parser.StatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceBody(@NotNull Java8Parser.InterfaceBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceBody(@NotNull Java8Parser.InterfaceBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassBodyDeclaration(@NotNull Java8Parser.ClassBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassBodyDeclaration(@NotNull Java8Parser.ClassBodyDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#lastFormalParameter}.
	 * @param ctx the parse tree
	 */
	void enterLastFormalParameter(@NotNull Java8Parser.LastFormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#lastFormalParameter}.
	 * @param ctx the parse tree
	 */
	void exitLastFormalParameter(@NotNull Java8Parser.LastFormalParameterContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#forControl}.
	 * @param ctx the parse tree
	 */
	void enterForControl(@NotNull Java8Parser.ForControlContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#forControl}.
	 * @param ctx the parse tree
	 */
	void exitForControl(@NotNull Java8Parser.ForControlContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeList}.
	 * @param ctx the parse tree
	 */
	void enterTypeList(@NotNull Java8Parser.TypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeList}.
	 * @param ctx the parse tree
	 */
	void exitTypeList(@NotNull Java8Parser.TypeListContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclaration(@NotNull Java8Parser.LocalVariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclaration(@NotNull Java8Parser.LocalVariableDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(@NotNull Java8Parser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(@NotNull Java8Parser.CompilationUnitContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#elementValue}.
	 * @param ctx the parse tree
	 */
	void enterElementValue(@NotNull Java8Parser.ElementValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#elementValue}.
	 * @param ctx the parse tree
	 */
	void exitElementValue(@NotNull Java8Parser.ElementValueContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterClassOrInterfaceType(@NotNull Java8Parser.ClassOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitClassOrInterfaceType(@NotNull Java8Parser.ClassOrInterfaceTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgumentsOrDiamond(@NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgumentsOrDiamond(@NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationTypeElementDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeElementDeclaration(@NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationTypeElementDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeElementDeclaration(@NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(@NotNull Java8Parser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(@NotNull Java8Parser.BlockStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationTypeBody}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeBody(@NotNull Java8Parser.AnnotationTypeBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationTypeBody}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeBody(@NotNull Java8Parser.AnnotationTypeBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#qualifiedNameList}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedNameList(@NotNull Java8Parser.QualifiedNameListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#qualifiedNameList}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedNameList(@NotNull Java8Parser.QualifiedNameListContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#creator}.
	 * @param ctx the parse tree
	 */
	void enterCreator(@NotNull Java8Parser.CreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#creator}.
	 * @param ctx the parse tree
	 */
	void exitCreator(@NotNull Java8Parser.CreatorContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMemberDeclaration(@NotNull Java8Parser.MemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMemberDeclaration(@NotNull Java8Parser.MemberDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationTypeElementRest}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeElementRest(@NotNull Java8Parser.AnnotationTypeElementRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationTypeElementRest}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeElementRest(@NotNull Java8Parser.AnnotationTypeElementRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#resourceSpecification}.
	 * @param ctx the parse tree
	 */
	void enterResourceSpecification(@NotNull Java8Parser.ResourceSpecificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#resourceSpecification}.
	 * @param ctx the parse tree
	 */
	void exitResourceSpecification(@NotNull Java8Parser.ResourceSpecificationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(@NotNull Java8Parser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(@NotNull Java8Parser.ConstructorDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#resource}.
	 * @param ctx the parse tree
	 */
	void enterResource(@NotNull Java8Parser.ResourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#resource}.
	 * @param ctx the parse tree
	 */
	void exitResource(@NotNull Java8Parser.ResourceContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#elementValuePair}.
	 * @param ctx the parse tree
	 */
	void enterElementValuePair(@NotNull Java8Parser.ElementValuePairContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#elementValuePair}.
	 * @param ctx the parse tree
	 */
	void exitElementValuePair(@NotNull Java8Parser.ElementValuePairContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void enterMethodBody(@NotNull Java8Parser.MethodBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void exitMethodBody(@NotNull Java8Parser.MethodBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void enterArrayInitializer(@NotNull Java8Parser.ArrayInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void exitArrayInitializer(@NotNull Java8Parser.ArrayInitializerContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#nonWildcardTypeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void enterNonWildcardTypeArgumentsOrDiamond(@NotNull Java8Parser.NonWildcardTypeArgumentsOrDiamondContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#nonWildcardTypeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void exitNonWildcardTypeArgumentsOrDiamond(@NotNull Java8Parser.NonWildcardTypeArgumentsOrDiamondContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(@NotNull Java8Parser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(@NotNull Java8Parser.PrimitiveTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#nonWildcardTypeArguments}.
	 * @param ctx the parse tree
	 */
	void enterNonWildcardTypeArguments(@NotNull Java8Parser.NonWildcardTypeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#nonWildcardTypeArguments}.
	 * @param ctx the parse tree
	 */
	void exitNonWildcardTypeArguments(@NotNull Java8Parser.NonWildcardTypeArgumentsContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#arrayCreatorRest}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreatorRest(@NotNull Java8Parser.ArrayCreatorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#arrayCreatorRest}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreatorRest(@NotNull Java8Parser.ArrayCreatorRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMemberDeclaration(@NotNull Java8Parser.InterfaceMemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMemberDeclaration(@NotNull Java8Parser.InterfaceMemberDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#genericConstructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGenericConstructorDeclaration(@NotNull Java8Parser.GenericConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#genericConstructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGenericConstructorDeclaration(@NotNull Java8Parser.GenericConstructorDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(@NotNull Java8Parser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(@NotNull Java8Parser.LiteralContext ctx);

	/**
	 * Enter a parse tree produced by {@link Java8Parser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlockStatementGroup(@NotNull Java8Parser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlockStatementGroup(@NotNull Java8Parser.SwitchBlockStatementGroupContext ctx);
}