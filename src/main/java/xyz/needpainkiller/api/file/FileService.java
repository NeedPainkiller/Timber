package xyz.needpainkiller.api.file;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.needpainkiller.api.file.dao.FileRepo;
import xyz.needpainkiller.api.file.error.FileException;
import xyz.needpainkiller.api.file.model.FileAuthorityType;
import xyz.needpainkiller.api.file.model.FileServiceType;
import xyz.needpainkiller.api.file.model.Files;
import xyz.needpainkiller.api.user.domain.model.User;
import xyz.needpainkiller.helper.TimeHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static xyz.needpainkiller.api.file.error.FileErrorCode.*;


@Slf4j
@Service
public class FileService {
    //    private static final int SQL_WHERE_IN_LIMIT = 2000;
    static final String DEF_FILE_SERVICE_NAME = "DEFAULT";
    static final Long DEF_FILE_SERVICE_ID = -1L;

    static final Predicate<Files> availableFile = Files::isUseYn;

    private static final Predicate<Files> notUsedFile = fileEntity -> {
        String fileServiceName = fileEntity.getFileService();
        Long fileServiceId = fileEntity.getFileServiceId();
        FileServiceType fileServiceType = fileEntity.getFileServiceType();
        return fileEntity.isFileExists() && (!fileEntity.isUseYn() || fileServiceName.equals(DEF_FILE_SERVICE_NAME) || fileServiceId.equals(DEF_FILE_SERVICE_ID) || fileServiceType.equals(FileServiceType.DEFAULT) || fileServiceType.equals(FileServiceType.NONE));
    };
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private FileRepo fileRepo;


    @Transactional
    public Files selectFile(String uuid) {
        return fileRepo.findByUuid(uuid).filter(availableFile).orElseThrow(() -> new FileException(FILE_DOWNLOAD_FILE_INFO_NOT_EXIST));
    }


    @Transactional
    public List<Files> selectFileList(List<String> uuidList) {
        try (Stream<Files> fileEntityStream = fileRepo.streamByUuidIn(uuidList)) {
            return fileEntityStream.filter(availableFile).toList();
        }
    }


    @Transactional
    public List<Files> selectFileNotUsed() {
        return fileRepo.findAllBy().stream().filter(notUsedFile).toList();
    }


    @Transactional
    public List<Files> selectServiceFileList(String fileServiceName) {
        return fileRepo.findByFileService(fileServiceName).stream().filter(availableFile).toList();
    }


    @Transactional
    public List<Files> selectServiceFileList(String fileServiceName, Long fileServiceId) {
        return fileRepo.findByFileServiceAndFileServiceId(fileServiceName, fileServiceId).stream().filter(availableFile).toList();
    }


    @Transactional
    public List<Files> selectServiceFileList(String fileServiceName, Long fileServiceId, FileServiceType fileServiceType) {
        return fileRepo.findByFileServiceAndFileServiceIdAndFileServiceType(fileServiceName, fileServiceId, fileServiceType).stream().filter(availableFile).toList();
    }


    public void validateFile(Files files) {
        if (Boolean.FALSE.equals(files.isUseYn())) {
            log.error("FILE_DOWNLOAD_CAN_NOT_USABLE");
            throw new FileException(FILE_DOWNLOAD_CAN_NOT_USABLE);
        }

        FileAuthorityType fileAuthorityType = files.getAccessAuthority();
        if (!FileAuthorityType.isExist(fileAuthorityType)) {
            log.error("FILE_DOWNLOAD_FILE_EMPTY_AUTH");
            throw new FileException(FILE_DOWNLOAD_FILE_EMPTY_AUTH);
        }
    }


    public void validateAuthority(Files files, User requester) {
        FileAuthorityType fileAuthorityType = files.getAccessAuthority();
        if (FileAuthorityType.PRIVATE.equals(fileAuthorityType) || FileAuthorityType.LOGON.equals(fileAuthorityType)) {
            if (FileAuthorityType.PRIVATE.equals(fileAuthorityType) && !files.getCreatedBy().equals(requester.getId())) {
                log.error("FILE_DOWNLOAD_FILE_NOT_YOU_OWN");
                throw new FileException(FILE_DOWNLOAD_FILE_NOT_YOU_OWN);
            }
        }
    }

    public void validateFileUpload(Files file) throws FileException {
        if (file == null) {
            throw new FileException(FILE_UPLOAD_INFO_NOT_EXIST);
        } else {
            String uuid = file.getUuid();
            String originalFileName = file.getOriginalFileName();
            if (Strings.isBlank(uuid) || Strings.isBlank(originalFileName)) { // 업로드 정보 존재하지 않음
                throw new FileException(FILE_UPLOAD_INFO_NOT_EXIST);
            }
            Long filePk = file.getId();
            if (filePk != null) {
                throw new FileException(FILE_UPLOAD_INFO_ALREADY_EXIST);
            }
        }
    }


    @Transactional
     public Files createFileFromMessageQueue(Files files) throws FileException {
        validateFileUpload(files);
        String uuid = files.getUuid();
        Optional<Files> existFile = fileRepo.findByUuid(uuid);
        if (existFile.isPresent()) {
            throw new FileException(FILE_UPLOAD_INFO_ALREADY_EXIST, files.getOriginalFileName());
        }
        files.setUseYn(true);
        files.setCreatedDate(TimeHelper.now());
        files.setDownloadCnt(0);
        files.setFileService(DEF_FILE_SERVICE_NAME);
        files.setFileServiceId(DEF_FILE_SERVICE_ID);
        files.setFileServiceType(FileServiceType.DEFAULT);
        files.setAccessAuthority(FileAuthorityType.PRIVATE);
        files.setFileExists(true);
        return fileRepo.save(files);
    }


    @Transactional
    public List<Files> createFileWithUpload(List<Files> filesList, User user) throws FileException {
        filesList.forEach(this::validateFileUpload);
        List<String> uuidList = filesList.stream().map(Files::getUuid).toList();
        List<Files> existFile = selectFileList(uuidList);
        if (!existFile.isEmpty()) {
            throw new FileException(FILE_UPLOAD_INFO_ALREADY_EXIST);
        }

        filesList.forEach(fileEntity -> {
            fileEntity.setUseYn(true);
            fileEntity.setCreatedBy(user.getId());
            fileEntity.setCreatedDate(TimeHelper.now());
            fileEntity.setDownloadCnt(0);
            fileEntity.setFileService(DEF_FILE_SERVICE_NAME);
            fileEntity.setFileServiceId(DEF_FILE_SERVICE_ID);
            fileEntity.setFileServiceType(FileServiceType.DEFAULT);
            fileEntity.setAccessAuthority(FileAuthorityType.PRIVATE);
            fileEntity.setFileExists(true);
        });
        return fileRepo.saveAll(filesList);
    }


    @Transactional
    public void increaseFileDownloadCnt(Files files) {
        files.setDownloadCnt(files.getDownloadCnt() + 1);
        fileRepo.save(files);
    }


    @Transactional
    public void upsertFileServiceByUuid(String fileServiceName, Long fileServiceId, FileServiceType fileServiceType, FileAuthorityType authorityType, List<String> uuidList) {

        // 기존에 사용중인 파일이 있으면 사용안함으로 변경
        List<Files> oldFileList = fileRepo.findByFileServiceAndFileServiceIdAndFileServiceType(fileServiceName, fileServiceId, fileServiceType);
        oldFileList.stream().filter(Files::isUseYn).forEach(fileEntity -> {
            fileEntity.setUseYn(false);
            fileRepo.save(fileEntity);
//            entityManager.detach(fileEntity);
        });

        if (uuidList.isEmpty()) {
            return;
        }
        uuidList = uuidList.stream().distinct().toList();
        List<Files> filesList = fileRepo.findByUuidIn(uuidList);
        if (filesList.isEmpty()) {
            return;
        }
        filesList.stream()
                .filter(fileEntity -> fileEntity.getFileService() == null || fileEntity.getFileService().equals(DEF_FILE_SERVICE_NAME) || fileEntity.getFileService().equals(fileServiceName))
                .filter(fileEntity -> fileEntity.getFileServiceId() == null || fileEntity.getFileServiceId().equals(DEF_FILE_SERVICE_ID) || fileEntity.getFileServiceId().equals(fileServiceId))
                .forEach(fileEntity -> {
                    fileEntity.setUseYn(true);
                    fileEntity.setFileService(fileServiceName);
                    fileEntity.setFileServiceId(fileServiceId);
                    fileEntity.setFileServiceType(fileServiceType);
                    fileEntity.setAccessAuthority(authorityType);
                });
        fileRepo.saveAll(filesList);
    }


    @Transactional
    public void deleteServiceFile(String fileServiceName, Long fileServiceId) {
        List<Files> filesList = selectServiceFileList(fileServiceName, fileServiceId);
        filesList.forEach(fileEntity -> fileEntity.setUseYn(false));
        fileRepo.saveAll(filesList);
    }


    @Transactional
    public void deleteServiceFile(String fileServiceName, Long fileServiceId, FileServiceType fileServiceType) {
        List<Files> filesList = selectServiceFileList(fileServiceName, fileServiceId, fileServiceType);
        filesList.forEach(fileEntity -> fileEntity.setUseYn(false));
        fileRepo.saveAll(filesList);
    }


    @Transactional
    public void deleteServiceFile(Files files) {
        files.setUseYn(false);
        fileRepo.save(files);
    }


    @Transactional
    public void deleteServiceFile(List<Files> filesList) {
        filesList.forEach(fileEntity -> fileEntity.setUseYn(false));
        fileRepo.saveAll(filesList);
    }


    @Transactional
    public void upsertFileExists(List<String> uuidList) {
        fileRepo.clearFileExists();
        try (Stream<Files> fileEntityStream = fileRepo.streamByUuidIn(uuidList)) {
            fileEntityStream.forEach(fileEntity -> {
                fileEntity.setFileExists(true);
                fileRepo.save(fileEntity);
            });
        }
    }
}

