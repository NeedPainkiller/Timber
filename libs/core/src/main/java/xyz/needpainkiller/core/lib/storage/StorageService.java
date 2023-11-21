package xyz.needpainkiller.core.lib.storage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface StorageService<F> {
    /**
     * 실제 파일 존재여부 확인
     *
     * @param filePath 파일 경로
     * @return 파일존재 여부
     */
    boolean isFileExist(String filePath);

    /**
     * 파일 복수 삭제
     * Async
     *
     * @param fileList 파일 도메인 객체 리스트
     * @return 삭제된 파일 리스트
     */
    List<F> remove(List<F> fileList);

    /**
     * 파일 삭제
     * Async
     *
     * @param file 파일 도메인 객체
     * @return 삭제여부
     */
    Boolean remove(F file);

    /**
     * 파일 업로드 및 저장
     *
     * @param request HttpServletRequest 객체
     */
    List<F> upload(HttpServletRequest request);

    /**
     * 파일 다운로드
     *
     * @param file     파일 도메인 객체
     * @param request  HttpServletRequest 객체
     * @param response HttpServletResponse 객체
     */
    void download(F file, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 파일 저장경로 디렉터리 생성
     */
    void mkdir();

    String getRealPathToSaved();

    long getUsableSpace();

    boolean isFileStorageCanSaved();


    List<String> findAllExistFileUuid();
}
