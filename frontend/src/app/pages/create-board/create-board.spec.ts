import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { CreateBoardPage } from './create-board';

describe('CreateBoardPage', () => {
  let component: CreateBoardPage;
  let fixture: ComponentFixture<CreateBoardPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateBoardPage],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateBoardPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
