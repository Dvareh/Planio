import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateTaskModal } from './create-task-modal';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';

describe('CreateTaskModal', () => {
  let component: CreateTaskModal;
  let fixture: ComponentFixture<CreateTaskModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateTaskModal],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateTaskModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
