package com.ecomapp.inventory_service.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ecomapp.inventory_service.annotations.AllowedRoles;
import com.ecomapp.inventory_service.constant.AppConstants;
import com.ecomapp.inventory_service.constant.RoleEnum;
import com.ecomapp.inventory_service.exception.CustomException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleGuardAspect {
  @Around("@annotation(allowedRoles)")
  public Object roleGuard(ProceedingJoinPoint joinPoint, AllowedRoles allowedRoles) throws Throwable {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if(attributes == null) {
      throw new CustomException("Request context not found", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    HttpServletRequest request = attributes.getRequest();
    String roleHeader = request.getHeader(AppConstants.X_USER_ROLE);

    if(roleHeader == null || roleHeader.isEmpty()) {
      throw new CustomException("Missing role header", HttpStatus.FORBIDDEN);
    }

    RoleEnum userRole = RoleEnum.valueOf(roleHeader.toUpperCase());
    RoleEnum[] allowedRolesGiven = allowedRoles.value();

    for(RoleEnum allowedRole : allowedRolesGiven) {
      if(userRole.equals(allowedRole)) {
        return joinPoint.proceed();
      }
    }

    throw new CustomException("Unauthorized access", HttpStatus.FORBIDDEN);
  }
}
