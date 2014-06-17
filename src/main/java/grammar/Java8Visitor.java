// Generated from Java8.g4 by ANTLR 4.2
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Java8Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface Java8Visitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Java8Parser#innerCreator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInnerCreator(@NotNull Java8Parser.InnerCreatorContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#genericMethodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericMethodDeclaration(@NotNull Java8Parser.GenericMethodDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(@NotNull Java8Parser.ExpressionListContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDeclaration(@NotNull Java8Parser.TypeDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#forUpdate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForUpdate(@NotNull Java8Parser.ForUpdateContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#annotation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotation(@NotNull Java8Parser.AnnotationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#enumConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumConstant(@NotNull Java8Parser.EnumConstantContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#importDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportDeclaration(@NotNull Java8Parser.ImportDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#annotationMethodOrConstantRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationMethodOrConstantRest(@NotNull Java8Parser.AnnotationMethodOrConstantRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#enumConstantName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumConstantName(@NotNull Java8Parser.EnumConstantNameContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#finallyBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFinallyBlock(@NotNull Java8Parser.FinallyBlockContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#variableDeclarators}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclarators(@NotNull Java8Parser.VariableDeclaratorsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#elementValuePairs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementValuePairs(@NotNull Java8Parser.ElementValuePairsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#interfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceMethodDeclaration(@NotNull Java8Parser.InterfaceMethodDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#interfaceBodyDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceBodyDeclaration(@NotNull Java8Parser.InterfaceBodyDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#enumConstants}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumConstants(@NotNull Java8Parser.EnumConstantsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#catchClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCatchClause(@NotNull Java8Parser.CatchClauseContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#constantExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantExpression(@NotNull Java8Parser.ConstantExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#enumDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumDeclaration(@NotNull Java8Parser.EnumDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#explicitGenericInvocationSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExplicitGenericInvocationSuffix(@NotNull Java8Parser.ExplicitGenericInvocationSuffixContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#typeParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParameter(@NotNull Java8Parser.TypeParameterContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#enumBodyDeclarations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumBodyDeclarations(@NotNull Java8Parser.EnumBodyDeclarationsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#typeBound}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeBound(@NotNull Java8Parser.TypeBoundContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#statementExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementExpression(@NotNull Java8Parser.StatementExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#variableInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableInitializer(@NotNull Java8Parser.VariableInitializerContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(@NotNull Java8Parser.BlockContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#genericInterfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericInterfaceMethodDeclaration(@NotNull Java8Parser.GenericInterfaceMethodDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalVariableDeclarationStatement(@NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#superSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperSuffix(@NotNull Java8Parser.SuperSuffixContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#fieldDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDeclaration(@NotNull Java8Parser.FieldDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#formalParameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameterList(@NotNull Java8Parser.FormalParameterListContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#explicitGenericInvocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExplicitGenericInvocation(@NotNull Java8Parser.ExplicitGenericInvocationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#parExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParExpression(@NotNull Java8Parser.ParExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#switchLabel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchLabel(@NotNull Java8Parser.SwitchLabelContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#typeParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParameters(@NotNull Java8Parser.TypeParametersContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#qualifiedName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedName(@NotNull Java8Parser.QualifiedNameContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#classDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDeclaration(@NotNull Java8Parser.ClassDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#annotationConstantRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationConstantRest(@NotNull Java8Parser.AnnotationConstantRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(@NotNull Java8Parser.ArgumentsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#constructorBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorBody(@NotNull Java8Parser.ConstructorBodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#formalParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameters(@NotNull Java8Parser.FormalParametersContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#typeArgument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArgument(@NotNull Java8Parser.TypeArgumentContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#forInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInit(@NotNull Java8Parser.ForInitContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#annotationTypeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationTypeDeclaration(@NotNull Java8Parser.AnnotationTypeDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(@NotNull Java8Parser.ExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#resources}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResources(@NotNull Java8Parser.ResourcesContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#formalParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameter(@NotNull Java8Parser.FormalParameterContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(@NotNull Java8Parser.TypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#elementValueArrayInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementValueArrayInitializer(@NotNull Java8Parser.ElementValueArrayInitializerContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#annotationName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationName(@NotNull Java8Parser.AnnotationNameContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#enhancedForControl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnhancedForControl(@NotNull Java8Parser.EnhancedForControlContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#annotationMethodRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationMethodRest(@NotNull Java8Parser.AnnotationMethodRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(@NotNull Java8Parser.PrimaryContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#classBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBody(@NotNull Java8Parser.ClassBodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#classOrInterfaceModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassOrInterfaceModifier(@NotNull Java8Parser.ClassOrInterfaceModifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#defaultValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefaultValue(@NotNull Java8Parser.DefaultValueContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#variableModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableModifier(@NotNull Java8Parser.VariableModifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#constDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstDeclaration(@NotNull Java8Parser.ConstDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#createdName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreatedName(@NotNull Java8Parser.CreatedNameContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceDeclaration(@NotNull Java8Parser.InterfaceDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#packageDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#constantDeclarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantDeclarator(@NotNull Java8Parser.ConstantDeclaratorContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#catchType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCatchType(@NotNull Java8Parser.CatchTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#typeArguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArguments(@NotNull Java8Parser.TypeArgumentsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#classCreatorRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassCreatorRest(@NotNull Java8Parser.ClassCreatorRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#modifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModifier(@NotNull Java8Parser.ModifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(@NotNull Java8Parser.StatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#interfaceBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceBody(@NotNull Java8Parser.InterfaceBodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBodyDeclaration(@NotNull Java8Parser.ClassBodyDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#lastFormalParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLastFormalParameter(@NotNull Java8Parser.LastFormalParameterContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#forControl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForControl(@NotNull Java8Parser.ForControlContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#typeList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeList(@NotNull Java8Parser.TypeListContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalVariableDeclaration(@NotNull Java8Parser.LocalVariableDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#compilationUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompilationUnit(@NotNull Java8Parser.CompilationUnitContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#elementValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementValue(@NotNull Java8Parser.ElementValueContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassOrInterfaceType(@NotNull Java8Parser.ClassOrInterfaceTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#typeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArgumentsOrDiamond(@NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#annotationTypeElementDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationTypeElementDeclaration(@NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#blockStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStatement(@NotNull Java8Parser.BlockStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#annotationTypeBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationTypeBody(@NotNull Java8Parser.AnnotationTypeBodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#qualifiedNameList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedNameList(@NotNull Java8Parser.QualifiedNameListContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#creator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreator(@NotNull Java8Parser.CreatorContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#memberDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberDeclaration(@NotNull Java8Parser.MemberDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#annotationTypeElementRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationTypeElementRest(@NotNull Java8Parser.AnnotationTypeElementRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#resourceSpecification}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResourceSpecification(@NotNull Java8Parser.ResourceSpecificationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#constructorDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorDeclaration(@NotNull Java8Parser.ConstructorDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#resource}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResource(@NotNull Java8Parser.ResourceContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#elementValuePair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementValuePair(@NotNull Java8Parser.ElementValuePairContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#methodBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodBody(@NotNull Java8Parser.MethodBodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#arrayInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayInitializer(@NotNull Java8Parser.ArrayInitializerContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#nonWildcardTypeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonWildcardTypeArgumentsOrDiamond(@NotNull Java8Parser.NonWildcardTypeArgumentsOrDiamondContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#primitiveType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveType(@NotNull Java8Parser.PrimitiveTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#nonWildcardTypeArguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonWildcardTypeArguments(@NotNull Java8Parser.NonWildcardTypeArgumentsContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#arrayCreatorRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayCreatorRest(@NotNull Java8Parser.ArrayCreatorRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceMemberDeclaration(@NotNull Java8Parser.InterfaceMemberDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#genericConstructorDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericConstructorDeclaration(@NotNull Java8Parser.GenericConstructorDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(@NotNull Java8Parser.LiteralContext ctx);

	/**
	 * Visit a parse tree produced by {@link Java8Parser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchBlockStatementGroup(@NotNull Java8Parser.SwitchBlockStatementGroupContext ctx);
}