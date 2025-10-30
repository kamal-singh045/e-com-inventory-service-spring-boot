package com.ecomapp.inventory_service.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ecomapp.inventory_service.constant.RoleEnum;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedRoles {
  RoleEnum[] value();
}
