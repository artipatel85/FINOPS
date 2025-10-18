/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.bean;

import java.io.Serializable;

/**
 *
 * @author bhaumikramani
 */
@lombok.Getter @lombok.Setter
public class EInvoiceResponse implements Serializable{
    private String Message;
    private String errorCode;
    private int status;
    private String traceId;
    private String irn;
    private String signedQRCode;
    private String signedInvoice;
    private String base64QRCode;
}
