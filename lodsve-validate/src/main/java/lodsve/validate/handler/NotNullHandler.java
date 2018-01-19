/*
 * Copyright (C) 2018  Sun.Hao
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package lodsve.validate.handler;

import lodsve.core.utils.ObjectUtils;
import lodsve.validate.annotations.NotNull;
import lodsve.validate.core.AbstractValidateHandler;
import lodsve.validate.exception.ErrorMessage;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * 不为空验证处理类.
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0
 * @createTime 12-11-26 下午8:51
 */
public class NotNullHandler extends AbstractValidateHandler {
    public NotNullHandler() throws IOException {
        super();
    }

    @Override
    protected ErrorMessage handle(Annotation annotation, Object value) {
        try {
            return getMessage(NotNull.class, getClass(), "not-null-error", ObjectUtils.isNotEmpty(value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
