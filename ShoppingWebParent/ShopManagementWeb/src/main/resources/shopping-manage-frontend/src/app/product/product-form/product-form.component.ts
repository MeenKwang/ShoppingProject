import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { BrandSelect } from 'src/app/model/brand-select.model';
import { CategorySelect } from 'src/app/model/category-select.model';
import { ProductService } from 'src/app/service/product/product.service';
import * as ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import { Product } from 'src/app/model/product.model';
import { faCircleXmark, faPlusSquare } from '@fortawesome/free-solid-svg-icons';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductImage } from 'src/app/model/product-image.model';
import { ProductDetail } from 'src/app/model/product-detail.model';


@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnInit {

  faCircleXmark = faCircleXmark;
  faPlusSquare = faPlusSquare;

  create = false;

  public Editor = ClassicEditor;

  submitted: boolean = false;
  productForm! : FormGroup;
  productFormModel : Product = {};
  brandsSelect : Array<BrandSelect> = [];
  categoriesSelect : Array<CategorySelect> = [];

  imageURL: string = '';

  constructor(
    private fb : FormBuilder,
    private productService : ProductService,
    private activatedRoute : ActivatedRoute,
    private router : Router,
  ) { }

  ngOnInit(): void {

    this.productForm = this.fb.group({
      id : new FormControl(null),
      name : new FormControl('', Validators.compose([Validators.minLength(3), Validators.maxLength(256), Validators.required])),
      alias : new FormControl('', Validators.compose([Validators.minLength(3), Validators.maxLength(256), Validators.required])),
      brand : new FormControl(null, Validators.required),
      category : new FormControl(null, Validators.required),
      enabled : new FormControl(true),
      inStock : new FormControl(true),
      cost : new FormControl(0.0),
      price : new FormControl(0.0),
      discountPercent : new FormControl(0.0),
      shortDescription : new FormControl('', Validators.compose([Validators.minLength(3), Validators.maxLength(2566), Validators.required])),
      fullDescription : new FormControl('', Validators.compose([Validators.minLength(3), Validators.maxLength(2566), Validators.required])),
      length : new FormControl(0.0),
      width : new FormControl(0.0),
      height : new FormControl(0.0),
      weight : new FormControl(0.0),
      mainImageFile : new FormControl(null),
      extraImages : this.fb.array([]),
      productDetails : this.fb.array([])
    });


    this.productService.listBrandsUsedInForm().subscribe(
      (response : any) => {
        this.brandsSelect = response;
      }
    );

    this.createOrEdit();

  }

  createOrEdit() {
    this.activatedRoute.paramMap.subscribe(s => {
      let id = s.get('id');
      console.log(id);
      if(id === null) {
        this.create = true;
        this.productForm.controls["mainImageFile"].setValidators(Validators.required);
        this.productForm.controls["extraImages"].setValidators(Validators.required);
        this.productForm.controls["productDetails"].setValidators(Validators.required);
        this.addProductDetail();
        this.addExtraImage();
        console.log("Create");
      } else {
        console.log("Edit");
        this.productService.getProductById(id).subscribe({
          next: (response) => {
            this.productFormModel = response;
            console.log(this.productFormModel);
            
            this.bindModelValueToForm();
          },
          error: (response) => {
            console.log("Error response from server!");
            this.router.navigate(["/products"]);
          }
        })
      }
    })
  }

  get extraImages() {
    return this.productForm.controls["extraImages"] as FormArray;
  }

  createProductDetail() {
    return this.fb.group({
      id : new FormControl(null),
      name : new FormControl('', Validators.compose([Validators.maxLength(255), Validators.required])),
      value : new FormControl('', Validators.compose([Validators.maxLength(255), Validators.required])),
      productId : new FormControl(null),
    })
  }

  addProductDetail() {
    this.productDetails.push(this.createProductDetail());
  }

  deleteProductDetail(index: number) {
    this.productDetails.removeAt(index);
  } 

  get productDetails() {
    return this.productForm.controls["productDetails"] as FormArray;
  }

  createExtraImage() {
    return this.fb.group({
      id : new FormControl(null),
      fileImg : new FormControl(null, Validators.required),
      urlImg : new FormControl(''),
      productId : new FormControl(null),
      modified : new FormControl(false)
    })
  }

  addExtraImage() {
    this.extraImages.push(this.createExtraImage());
  }

  deleteExtraImage(imageIndex: number) {
    this.extraImages.removeAt(imageIndex);
  } 

  createAlias() {
    this.productForm.controls["alias"].patchValue(this.productForm.controls["name"].value.replaceAll(" ", "_"));
  }

  listCategoriesUsedInForm() {
    this.productForm.controls["category"].patchValue(null);
    let id : any = this.productForm.controls["brand"].value;
    this.productService.listCategoriesUsedInForm(id).subscribe(
      (response : any) => {
        this.categoriesSelect = response;
      }
    )
  }

  get f(): { [key: string]: AbstractControl } {
    return this.productForm.controls;
  }

  submit() {
    console.log(this.findInvalidControls());
    this.submitted = true;
    if(this.productForm.invalid) {
      return;
    }
    let extraImageFiles : File[] = [];
    const extraImagesArray = this.productForm.controls["extraImages"] as FormArray;
    for(let i of extraImagesArray.controls) {
        if(i instanceof FormGroup) {
          if(i.controls["fileImg"].value !== null) {
            extraImageFiles.push(i.controls["fileImg"].value);
          }
        }
    }
    this.bindFormValueToModel();
    
    console.log(extraImageFiles);

    let mainImageFile = this.productForm.controls["mainImageFile"].value as File;

    if(mainImageFile !== null) console.log(mainImageFile);
    else console.log("NULL")
    
    this.productService.saveProduct(this.productFormModel, mainImageFile, extraImageFiles).subscribe(
      (response : any) => alert("OK")
    )
  }

  bindFormValueToModel() {
    this.productFormModel.name = this.productForm.controls["name"].value;
    this.productFormModel.alias = this.productForm.controls["alias"].value;
    let brandId = this.productForm.controls["brand"].value;
    let brand : BrandSelect = new BrandSelect(brandId, "");
    this.productFormModel.brand = brand;
    let categoryId = this.productForm.controls["category"].value;
    let category : CategorySelect = new CategorySelect(categoryId, "");
    this.productFormModel.category = category;
    this.productFormModel.enabled = this.productForm.controls["enabled"].value;
    this.productFormModel.inStock = this.productForm.controls["inStock"].value;
    this.productFormModel.cost = this.productForm.controls["cost"].value;
    this.productFormModel.price = this.productForm.controls["price"].value;
    this.productFormModel.discountPercent = this.productForm.controls["discountPercent"].value;
    this.productFormModel.shortDescription = this.productForm.controls["shortDescription"].value;
    this.productFormModel.fullDescription = this.productForm.controls["fullDescription"].value;
    //Extra Product Images
    let productImages : Array<ProductImage> = [];
    for(let i of (this.productForm.controls["extraImages"] as FormArray).controls) {
      if(i instanceof FormGroup) {
        let productImage : ProductImage = new ProductImage();
        productImage.id = i.controls["id"].value;
        productImage.name = "";
        productImage.productId = i.controls["productId"].value;
        productImage.modified = i.controls["modified"].value;
        productImages.push(productImage);
      }
    }
    this.productFormModel.extraImages = productImages;
    //Product Details
    let productDetails : Array<ProductDetail> = [];
    for(let i of (this.productForm.controls["productDetails"] as FormArray).controls) {
      if(i instanceof FormGroup) {
        let productDetail : ProductDetail = new ProductDetail();
        productDetail.id = i.controls["id"].value;
        productDetail.name = i.controls["name"].value;
        productDetail.value = i.controls["value"].value;
        productDetail.productId = i.controls["productId"].value;
        productDetails.push(productDetail);
      }
    }
    this.productFormModel.productDetails = productDetails;

    this.productFormModel.length = this.productForm.controls["length"].value;
    this.productFormModel.width = this.productForm.controls["width"].value;
    this.productFormModel.height = this.productForm.controls["height"].value;
    this.productFormModel.weight = this.productForm.controls["weight"].value;
  }
  

  public showPreview(event : any) {
    const file = event.target.files[0];
    if(file) {
      // File Preview
      const reader = new FileReader();
      reader.onload = () => {
        this.imageURL = reader.result as string;
        this.productForm.get('mainImageFile')?.patchValue(file);
      }
      reader.readAsDataURL(file);
    } else {
      this.productForm.patchValue({
        mainImageFile: null
      });
      this.imageURL = '';
    }
  }

  showExtraImagePreview(event : any, index : number) {
    const file = event.target.files[0];
    const formArray = this.productForm.controls["extraImages"] as FormArray;
    const formGroup = formArray.at(index) as FormGroup;
    const idControl = formGroup.controls["id"];
    const fileControl = formGroup.controls["fileImg"];
    const urlControl = formGroup.controls["urlImg"];
    const modified = formGroup.controls["modified"];
    if(file) {
      // Check modified
      if(idControl.value !== null) {
        modified.patchValue(true);
      }
      // File Preview
      const reader = new FileReader();
      reader.onload = () => {
        fileControl.patchValue(file);
        urlControl.patchValue(reader.result + '');
        console.log(urlControl);
      }
      reader.readAsDataURL(file);
    } else {
      fileControl.patchValue(null);
      urlControl.patchValue('')
    }
  }

  getUrlImg(index : number) {
    const formArray = this.productForm.controls["extraImages"] as FormArray;
    const formGroup = formArray.at(index) as FormGroup;
    const urlControl = formGroup.controls["urlImg"];
    return urlControl.value;
  }

  checkExtraImagesFormSize() {
    const formArray = this.productForm.controls["extraImages"] as FormArray;
    return formArray.length;
  }

  checkProductDetailsFormSize() {
    const formArray = this.productForm.controls["productDetails"] as FormArray;
    return formArray.length;
  }

  bindModelValueToForm() {
    this.productForm.controls["id"].patchValue(this.productFormModel.id);
    this.productForm.controls["name"].patchValue(this.productFormModel.name);
    this.productForm.controls["alias"].patchValue(this.productFormModel.alias);
    this.productForm.controls["brand"].patchValue(this.productFormModel.brand?.id);
    this.listCategoriesUsedInForm();
    this.productForm.controls["category"].patchValue(this.productFormModel.category?.id);
    this.productForm.controls["enabled"].patchValue(this.productFormModel.enabled);
    this.productForm.controls["inStock"].patchValue(this.productFormModel.inStock);
    this.productForm.controls["cost"].patchValue(this.productFormModel.cost);
    this.productForm.controls["price"].patchValue(this.productFormModel.price);
    this.productForm.controls["discountPercent"].patchValue(this.productFormModel.discountPercent);
    this.productForm.controls["shortDescription"].patchValue(this.productFormModel.shortDescription);
    this.productForm.controls["fullDescription"].patchValue(this.productFormModel.fullDescription);
    this.productForm.controls["mainImageFile"].patchValue(null);
    this.imageURL = "product-images/" + this.productFormModel.id + "/" + this.productFormModel.mainImage;

    this.productFormModel.extraImages?.forEach((extraImage, index) => {
      const imageUrl = "product-images/" + this.productFormModel.id + "/extras/" + extraImage.name;
      let fg : FormGroup = this.fb.group({
        id : new FormControl(extraImage.id),
        fileImg : new FormControl(null),
        urlImg : new FormControl(imageUrl),
        productId : new FormControl(extraImage.productId),
        modified : new FormControl(false)
      });
      (this.productForm.controls["extraImages"] as FormArray).push(fg);
    })

    this.productFormModel.productDetails?.forEach((productDetail, index) => {
      let fg : FormGroup = this.fb.group({
        id : new FormControl(productDetail.id),
        name : new FormControl(productDetail.name, Validators.compose([Validators.maxLength(255), Validators.required])),
        value : new FormControl(productDetail.value, Validators.compose([Validators.maxLength(255), Validators.required])),
        productId : new FormControl(productDetail.productId),
      });
      (this.productForm.controls["productDetails"] as FormArray).push(fg);
    })

    this.productForm.controls["length"].patchValue(this.productFormModel.length);
    this.productForm.controls["width"].patchValue(this.productFormModel.width);
    this.productForm.controls["height"].patchValue(this.productFormModel.height);
    this.productForm.controls["weight"].patchValue(this.productFormModel.weight);
    console.log(this.productFormModel.extraImages);
  }

  public findInvalidControls() {
    const invalid = [];
    const controls = this.productForm.controls;
    for (const name in controls) {
        if (controls[name].invalid) {
            invalid.push(name);
        }
    }
    return invalid;
}
}
