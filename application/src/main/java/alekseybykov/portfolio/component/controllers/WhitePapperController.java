//
// Feel free to use these solutions in your work.
//
package alekseybykov.portfolio.component.controllers;

import alekseybykov.portfolio.component.dto.*;
import alekseybykov.portfolio.component.entities.FileTransferObject;
import alekseybykov.portfolio.component.entities.WhitePapper;
import alekseybykov.portfolio.component.mappings.ScreenDocumentMapper;
import alekseybykov.portfolio.component.mappings.WhitePapperMapper;
import alekseybykov.portfolio.component.services.metadata.WhitepapperMetadataService;
import alekseybykov.portfolio.component.services.whitepapper.WhitePapperService;
import alekseybykov.portfolio.component.utils.FileValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * @author  aleksey.n.bykov@gmail.com
 * @version 1.0
 * @since   2019-08-25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/white-pappers")
@Api(tags = "whitepappers", description = "Controller for working with whitepappers")
public class WhitePapperController {

    private final WhitePapperService whitePapperService;
    private final WhitePapperMapper whitePapperMapper;
    private final WhitepapperMetadataService whitepapperMetadataService;
    private final FileValidator fileValidator;
    private final ScreenDocumentMapper screenDocumentMapper;

    @ApiOperation(value = "Upload file with metadata")
    @PostMapping("/upload")
    @SneakyThrows
    public ResponseEntity<FileUploadDto> uploadWhitePapper(
            @ApiParam("Whitepapper's file")
            @RequestPart(name = "file") MultipartFile file,
            @ApiParam("Name of whitepapper")
            @RequestParam(name = "name") String name,
            @ApiParam("Type of whitepapper")
            @RequestParam(name = "type") String type,
            @ApiParam("Registration number of whitepapper")
            @RequestParam(name = "registrationNumber") String registrationNumber,
            @ApiParam("Registration date of whitepapper")
            @RequestParam(name = "registrationDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate registrationDate,
            @ApiParam("Identifier of the scope of whitepapper")
            @RequestParam(name = "scopeId") Long scopeId,
            @ApiParam("Name of the scope of whitepapper")
            @RequestParam(name = "scopeName") String scopeName) {
        fileValidator.validate(file);
        ScopeDto scopeDto = ScopeDto.builder().id(scopeId).name(scopeName).build();
        WhitePapperMetadataDto whitePapperMetadataDto = WhitePapperMetadataDto.builder().name(name).type(type)
                .registrationNumber(registrationNumber).registrationDate(registrationDate)
                .scope(scopeDto).build();

        FileTransferObject fileTransferObject = FileTransferObject.builder()
                .fileName(file.getOriginalFilename())
                .stream(file.getInputStream()).build();

        Long id = whitePapperService.upload(fileTransferObject,
                whitePapperMapper.toEntity(whitePapperMetadataDto));
        return ResponseEntity.ok(FileUploadDto.builder().id(id).build());
    }

    @ApiOperation(value = "Get all whitepappers [e.g, for displaying in screen, in table view]")
    @GetMapping()
    public Page<ScreenDocumentDto> getAllWhitepappersForScreen(
            @ApiParam("Size of page starting from 0") @RequestParam(value = "page") final Integer page,
            @ApiParam("Page size") @RequestParam(value = "size") final Integer size) {
        Page<WhitePapper> data = whitePapperService.findAllWhitepappers(page, size);
        return new PageImpl<>(screenDocumentMapper.toListDto(data.getContent()),
                data.getPageable(), data.getTotalElements());
    }

    @ApiOperation(value = "Get whitepapper by identifier [e.g, for displaying in edit view]")
    @GetMapping("/get-whitepapper")
    public ResponseEntity<ScreenDocumentDto> getMetadata(
            @ApiParam("Whitepapper identifier") @RequestParam("id") Long id) {
        WhitePapper whitePapper = whitePapperService.getById(id);
        ScreenDocumentDto screenDocumentDto = screenDocumentMapper.toDto(whitePapper);
        return ResponseEntity.ok(screenDocumentDto);
    }

    @ApiOperation(value = "Update whitepapper's metadata by identifier [e.g, data is submited from edit view]")
    @PutMapping("/update-metadata")
    public ResponseEntity<Long> updateMetadata(
            @ApiParam("Identifier of the whitepapper") @RequestParam("id") Long id,
            @ApiParam("Updated metadata") @RequestBody WhitePapperMetadataDto dto) {
        return ResponseEntity.ok(whitepapperMetadataService.save(whitePapperMapper.toEntity(dto), id).getId());
    }

    @PostMapping()
    @ApiOperation(value = "Deleting white pappers by dentifiers")
    public void deleteByIds(@ApiParam("list of white pappers") @RequestBody IdsDto idsDto) {
        whitePapperService.deleteByIds(idsDto.getIds());
    }
}
