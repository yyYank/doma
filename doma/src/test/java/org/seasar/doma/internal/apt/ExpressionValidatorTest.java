/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.internal.apt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import org.seasar.doma.internal.apt.dao.ExpressionValidationDao;
import org.seasar.doma.internal.apt.declaration.TypeDeclaration;
import org.seasar.doma.internal.apt.entity.Emp;
import org.seasar.doma.internal.expr.ExpressionParser;
import org.seasar.doma.internal.expr.node.ExpressionNode;
import org.seasar.doma.internal.message.DomaMessageCode;

public class ExpressionValidatorTest extends AptTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addSourcePath("src/test/java");
    }

    public void testVariableNotFound() throws Exception {
        Class<?> target = ExpressionValidationDao.class;
        addCompilationUnit(target);
        compile();

        ExecutableElement methodElement = createMethodElement(target,
                "testEmp", Emp.class);
        Map<String, TypeMirror> parameterTypeMap = createParameterTypeMap(methodElement);
        ExpressionValidator validator = new ExpressionValidator(
                getProcessingEnvironment(), methodElement, parameterTypeMap);

        ExpressionNode node = new ExpressionParser("notFound").parse();
        try {
            validator.validate(node);
            fail();
        } catch (AptException expected) {
            System.out.println(expected);
            assertEquals(DomaMessageCode.DOMA4067, expected.getMessageCode());
        }
    }

    public void testMethodNotFound() throws Exception {
        Class<?> target = ExpressionValidationDao.class;
        addCompilationUnit(target);
        compile();

        ExecutableElement methodElement = createMethodElement(target,
                "testEmp", Emp.class);
        Map<String, TypeMirror> parameterTypeMap = createParameterTypeMap(methodElement);
        ExpressionValidator validator = new ExpressionValidator(
                getProcessingEnvironment(), methodElement, parameterTypeMap);

        ExpressionNode node = new ExpressionParser(
                "emp.notFound(1, \"aaa\".length())").parse();
        try {
            validator.validate(node);
            fail();
        } catch (AptException expected) {
            System.out.println(expected);
            assertEquals(DomaMessageCode.DOMA4071, expected.getMessageCode());
        }
    }

    public void testConstructorNotFound() throws Exception {
        Class<?> target = ExpressionValidationDao.class;
        addCompilationUnit(target);
        compile();

        ExecutableElement methodElement = createMethodElement(target,
                "testEmp", Emp.class);
        Map<String, TypeMirror> parameterTypeMap = createParameterTypeMap(methodElement);
        ExpressionValidator validator = new ExpressionValidator(
                getProcessingEnvironment(), methodElement, parameterTypeMap);

        ExpressionNode node = new ExpressionParser("new java.lang.String(1, 2)")
                .parse();
        try {
            validator.validate(node);
            fail();
        } catch (AptException expected) {
            System.out.println(expected);
            assertEquals(DomaMessageCode.DOMA4115, expected.getMessageCode());
        }
    }

    public void testFieldAccess() throws Exception {
        Class<?> target = ExpressionValidationDao.class;
        addCompilationUnit(target);
        compile();

        ExecutableElement methodElement = createMethodElement(target,
                "testEmp", Emp.class);
        Map<String, TypeMirror> parameterTypeMap = createParameterTypeMap(methodElement);
        ExpressionValidator validator = new ExpressionValidator(
                getProcessingEnvironment(), methodElement, parameterTypeMap);

        ExpressionNode node = new ExpressionParser("emp.id").parse();
        TypeDeclaration result = validator.validate(node);
        assertFalse(result.isUnknownType());
    }

    public void testMethodAccess() throws Exception {
        Class<?> target = ExpressionValidationDao.class;
        addCompilationUnit(target);
        compile();

        ExecutableElement methodElement = createMethodElement(target,
                "testEmp", Emp.class);
        Map<String, TypeMirror> parameterTypeMap = createParameterTypeMap(methodElement);
        ExpressionValidator validator = new ExpressionValidator(
                getProcessingEnvironment(), methodElement, parameterTypeMap);

        ExpressionNode node = new ExpressionParser("emp.getId()").parse();
        TypeDeclaration result = validator.validate(node);
        assertFalse(result.isUnknownType());
    }

    public void testConstructorAccess() throws Exception {
        Class<?> target = ExpressionValidationDao.class;
        addCompilationUnit(target);
        compile();

        ExecutableElement methodElement = createMethodElement(target,
                "testEmp", Emp.class);
        Map<String, TypeMirror> parameterTypeMap = createParameterTypeMap(methodElement);
        ExpressionValidator validator = new ExpressionValidator(
                getProcessingEnvironment(), methodElement, parameterTypeMap);

        ExpressionNode node = new ExpressionParser(
                "emp.id == new java.lang.Integer(1)").parse();
        TypeDeclaration result = validator.validate(node);
        assertFalse(result.isUnknownType());
    }

    public void testMethodAccess_withArguments() throws Exception {
        Class<?> target = ExpressionValidationDao.class;
        addCompilationUnit(target);
        compile();

        ExecutableElement methodElement = createMethodElement(target,
                "testEmp", Emp.class);
        Map<String, TypeMirror> parameterTypeMap = createParameterTypeMap(methodElement);
        ExpressionValidator validator = new ExpressionValidator(
                getProcessingEnvironment(), methodElement, parameterTypeMap);

        ExpressionNode node = new ExpressionParser("emp.add(2, 3)").parse();
        TypeDeclaration result = validator.validate(node);
        assertFalse(result.isUnknownType());
    }

    public void testEqOperator() throws Exception {
        Class<?> target = ExpressionValidationDao.class;
        addCompilationUnit(target);
        compile();

        ExecutableElement methodElement = createMethodElement(target,
                "testEmp", Emp.class);
        Map<String, TypeMirror> parameterTypeMap = createParameterTypeMap(methodElement);
        ExpressionValidator validator = new ExpressionValidator(
                getProcessingEnvironment(), methodElement, parameterTypeMap);

        ExpressionNode node = new ExpressionParser("emp.add(2, 3) == 5")
                .parse();
        TypeDeclaration result = validator.validate(node);
        assertFalse(result.isUnknownType());
    }

    public void testUnreferencedParameter() throws Exception {
        Class<?> target = ExpressionValidationDao.class;
        addCompilationUnit(target);
        compile();

        ExecutableElement methodElement = createMethodElement(target,
                "testEmp", Emp.class);
        Map<String, TypeMirror> parameterTypeMap = createParameterTypeMap(methodElement);
        ExpressionValidator validator = new ExpressionValidator(
                getProcessingEnvironment(), methodElement, parameterTypeMap);

        ExpressionNode node = new ExpressionParser("true").parse();
        validator.validate(node);
        assertFalse(validator.getValidatedParameterNames().contains("emp"));

    }

    protected ExecutableElement createMethodElement(Class<?> clazz,
            String methodName, Class<?>... parameterClasses) {
        ProcessingEnvironment env = getProcessingEnvironment();
        TypeElement typeElement = ElementUtil.getTypeElement(clazz, env);
        for (TypeElement t = typeElement; t != null
                && t.asType().getKind() != TypeKind.NONE; t = TypeUtil
                .toTypeElement(t.getSuperclass(), env)) {
            for (ExecutableElement methodElement : ElementFilter.methodsIn(t
                    .getEnclosedElements())) {
                if (!methodElement.getSimpleName().contentEquals(methodName)) {
                    continue;
                }
                List<? extends VariableElement> parameterElements = methodElement
                        .getParameters();
                if (parameterElements.size() != parameterClasses.length) {
                    continue;
                }
                for (int i = 0; i < parameterElements.size(); i++) {
                    TypeMirror parameterType = parameterElements.get(i)
                            .asType();
                    Class<?> parameterClass = parameterClasses[i];
                    if (!TypeUtil
                            .isSameType(parameterType, parameterClass, env)) {
                        return null;
                    }
                }
                return methodElement;
            }
        }
        return null;
    }

    protected Map<String, TypeMirror> createParameterTypeMap(
            ExecutableElement methodElement) {
        Map<String, TypeMirror> result = new HashMap<String, TypeMirror>();
        ProcessingEnvironment env = getProcessingEnvironment();
        for (VariableElement parameter : methodElement.getParameters()) {
            String name = parameter.getSimpleName().toString();
            TypeMirror type = parameter.asType();
            if (TypeUtil.isSameType(type, List.class, env)) {
                DeclaredType declaredType = TypeUtil.toDeclaredType(type, env);
                TypeMirror elementType = declaredType.getTypeArguments().get(0);
                result.put(name, elementType);
            } else {
                result.put(name, type);
            }
        }
        return result;
    }

}