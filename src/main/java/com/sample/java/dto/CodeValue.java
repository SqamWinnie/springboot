
package com.java.dto;

import lombok.Data;


/**
 * CodeValueDTO.
 *
 * @author runbai.chen
 */
@Data
public class CodeValue{

    private static final long serialVersionUID = 7078027762943933806L;

    private Long codeId;

    private Long codeValueId;

    private String description;

    private String meaning;

    private String value;

    private Long orderSeq;

    private String tag;

    private String enabledFlag;

    private Long parentCodeValueId;

    private String parentCodeValueMeaning;

}

