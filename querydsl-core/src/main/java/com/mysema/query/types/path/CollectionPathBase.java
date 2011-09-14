package com.mysema.query.types.path;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.annotation.Nullable;

import com.mysema.query.types.ExpressionException;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.expr.CollectionExpressionBase;
import com.mysema.query.types.expr.SimpleExpression;

/**
 * CollectionPath is a base class for collection typed paths
 * 
 * @author tiwe
 *
 * @param <E> component type
 * @param <Q> component query type
 */
public abstract class CollectionPathBase<C extends Collection<E>, E, Q extends SimpleExpression<? super E>> 
    extends CollectionExpressionBase<C, E> implements Path<C>{

    private static final long serialVersionUID = -9004995667633601298L;

    public CollectionPathBase(Class<? extends C> type) {
        super(type);
    }

    @Nullable
    private transient volatile Constructor<?> constructor;
    
    private volatile boolean usePathInits = false;
    
    public abstract Q any();
    
    @SuppressWarnings("unchecked")
    protected Q newInstance(Class<Q> queryType, PathMetadata<?> pm){
        try{
            if (constructor == null) {
                if (Constants.isTyped(queryType)){
                    try {
                        constructor = queryType.getConstructor(Class.class, PathMetadata.class, PathInits.class);    
                        usePathInits = true;
                    } catch (NoSuchMethodException e) {
                        constructor = queryType.getConstructor(Class.class, PathMetadata.class);
                    }                    
                }else{
                    try {
                        constructor = queryType.getConstructor(PathMetadata.class, PathInits.class);
                        usePathInits = true;
                    } catch (NoSuchMethodException e) {
                        constructor = queryType.getConstructor(PathMetadata.class);
                    }    
                }
            }
            if (Constants.isTyped(queryType)){
                if (usePathInits) {
                    return (Q)constructor.newInstance(getElementType(), pm, PathInits.DIRECT);
                } else {
                    return (Q)constructor.newInstance(getElementType(), pm);    
                }
                
            }else{
                if (usePathInits) {
                    return (Q)constructor.newInstance(pm, PathInits.DIRECT);    
                } else {
                    return (Q)constructor.newInstance(pm);
                }                
            }
        } catch (NoSuchMethodException e) {
            throw new ExpressionException(e);
        } catch (InstantiationException e) {
            throw new ExpressionException(e);
        } catch (IllegalAccessException e) {
            throw new ExpressionException(e);
        } catch (InvocationTargetException e) {
            throw new ExpressionException(e);
        }
        
    }

}
