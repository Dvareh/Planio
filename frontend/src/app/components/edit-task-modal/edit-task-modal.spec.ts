import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditTaskModal } from './edit-task-modal';

describe('EditTaskModal', () => {
  let component: EditTaskModal;
  let fixture: ComponentFixture<EditTaskModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditTaskModal],
    }).compileComponents();

    fixture = TestBed.createComponent(EditTaskModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
