import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteTaskModal } from './delete-task-modal';

describe('DeleteTaskModal', () => {
  let component: DeleteTaskModal;
  let fixture: ComponentFixture<DeleteTaskModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteTaskModal],
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteTaskModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
