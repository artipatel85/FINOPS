/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.util;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;


import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.time.Duration;

/**
 *
 * @author Bhaumik
 */
public class AzureBlobUtil {

    public static BlobContainerClient getBlobContainerClient(String containerName, String endPoint){
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().endpoint(endPoint).buildClient();
        return blobServiceClient.getBlobContainerClient(containerName);
    }


    public static String uploadFile(String fileName, ByteArrayInputStream is, int length, String containerName, String endPoint){
        BlobClient blobClient = getBlobContainerClient(containerName, endPoint).getBlobClient(fileName);
        blobClient.upload(is, length, true);
        return blobClient.getBlobUrl();
    }

    public static void uploadFromFile(String filePath, String fileName, String containerName, String endPoint){
        long timeMillis = System.currentTimeMillis();
        System.out.println("Starting Blob upload");
        BlobClient blobClient = getBlobContainerClient(containerName, endPoint).getBlobClient(fileName);
        long blockSize = 100 * 1024 * 1024;

        ParallelTransferOptions parallelTransferOptions = new ParallelTransferOptions()
                .setBlockSizeLong(blockSize).setMaxConcurrency(6)
                .setProgressReceiver(new ProgressReceiver() {
                    @Override
                    public void reportProgress(long bytesTransferred) {
                        System.out.println("uploaded:" + bytesTransferred);
                    }
                });
        BlobHttpHeaders headers = new BlobHttpHeaders().setContentLanguage("en-US").setContentType("binary");
        blobClient.uploadFromFile(filePath,parallelTransferOptions, headers, null, AccessTier.COOL,
                new BlobRequestConditions(), Duration.ofMinutes(30));
        System.out.println(blobClient.getBlobUrl());
        System.out.println((System.currentTimeMillis() - timeMillis) / 1000);
    }

    public static void main(String[] args) throws ParseException {
        String endpoint = "https://shikharbkup1.blob.core.windows.net/?sv=2021-06-08&ss=bf&srt=sco&sp=rwdlaciytfx&se=2023-10-16T11:00:00Z&st=2022-10-16T03:00:00Z&spr=https&sig=g%2Fpm3F5ODmlhu5EHgtHYS52r6QhRSr4iqhwgbJXLgnA%3D";
        endpoint = "https://shikharbkup1.blob.core.windows.net/?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2027-01-05T01:48:20Z&st=2025-01-04T17:48:20Z&spr=https,http&sig=TyiD0PMMbBLUnDw1RqXEnAIq03Ny5dtErFTR67eksO4%3D";
        AzureBlobUtil.uploadFromFile("C:\\Bhaumik\\db.properties", "db.properties" , "partner/"+ DateUtil.getSystemDate(), endpoint);
        //AzureBlobUtil.deleteBlob("SHIK2022_DIFF-09-30.bak","backup/2022-11-17", endpoint);
        for(int i=4; i<28; i++){
            String ddate = DateUtil.previousDateString("2022-12-17", i);
            System.out.println(ddate);
            //AzureBlobUtil.deleteContainer(ddate, endpoint);
        }

    }

    public static void deleteBlob(String fileName, String containerName, String endPoint){
        BlobClient blobClient = getBlobContainerClient(containerName, endPoint).getBlobClient(fileName);
        blobClient.delete();
    }

    public static void deleteContainer(String containerName, String endPoint){
        String cstring = "BlobEndpoint=https://shikharbkup1.blob.core.windows.net/;QueueEndpoint=https://shikharbkup1.queue.core.windows.net/;FileEndpoint=https://shikharbkup1.file.core.windows.net/;TableEndpoint=https://shikharbkup1.table.core.windows.net/;SharedAccessSignature=sv=2021-06-08&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2023-12-17T14:42:37Z&st=2022-12-17T06:42:37Z&spr=https&sig=AKycWkYZkfhjPxsAoaXFLnISqgTJhXS3DTT8xAhUKyw%3D";
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(cstring).buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("backup");
        System.out.println("\nListing blobs...");

        //BlobContainerClient blobContainerClient = getBlobContainerClient(containerName, endPoint);
        for(BlobItem itm : containerClient.listBlobs()){
            String blob = itm.getName();
            if(blob.startsWith(containerName)){
                System.out.println(blob);
                AzureBlobUtil.deleteBlob(blob, "backup", endPoint);
            }

        }
        //blobContainerClient.delete();
    }

    public static void downloadBlob(String fileName, String containerName, String endPoint, String localPath) throws FileNotFoundException {
        BlobClient blobClient = getBlobContainerClient(containerName, endPoint).getBlobClient(fileName);
        blobClient.download(new FileOutputStream(localPath+fileName));
    }
}
